package dev.son.movie.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityAccountBinding
import dev.son.movie.network.models.user.User
import dev.son.movie.ui.login.AuthViewAction
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.login.AuthViewState
import dev.son.movie.utils.DialogUtil
import dev.son.movie.utils.PermissionUtils
import dev.son.movie.utils.convertBitmapToBase64
import dev.son.movie.utils.hide
import dev.son.movie.utils.setSingleClickListener
import dev.son.movie.utils.show
import kotlinx.coroutines.launch
import javax.inject.Inject


class AccountActivity : TrackingBaseActivity<ActivityAccountBinding>(), AuthViewModel.Factory {
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 2

    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory
    private val authViewModel: AuthViewModel by viewModel()
    private var id: String? = null
    private var image: Uri? = null
    private var user: User = User()
    private var updateData = HashMap<String, Any>()

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        registerResult()
        setUpUi()
        resultUpdate()
    }

    private fun resultUpdate() {
        authViewModel.subscribe(this) {
            when (it.upDateUser) {
                is Success -> {
                    Toast.makeText(this, "${it.upDateUser.invoke().message}", Toast.LENGTH_SHORT)
                        .show()
                    authViewModel.handleRemoveUpdateUser()
                    views.loading.hide()
                }

                is Fail -> {
                    Toast.makeText(this, "${it.upDateUser.error}", Toast.LENGTH_SHORT).show()
                    views.editEmail.setText(user.email)
                    views.editDislayName.setText(user.name)
                    views.editDob.setText(user.birthday)
                    authViewModel.handleRemoveUpdateUser()
                    views.loading.hide()
                }

                else -> {}
            }
            when (it.upLoadImage) {
                is Success -> {
                    val updateData = HashMap<String, Any>()
                    updateData["photoURL"] = it.upLoadImage.invoke()
                    authViewModel.handle(AuthViewAction.upDateUser(updateData))
                    authViewModel.handleRemoveUpLoadr()
                }

                is Fail -> {
                    Toast.makeText(this, "Lỗi khi cập nhập", Toast.LENGTH_SHORT).show()
                    authViewModel.handleRemoveUpLoadr()
                    views.loading.hide()
                }

                else -> {}
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpUi() {
        lifecycleScope.launch {
            userPreferences.user.collect {
                if (it != null) {
                    views.editEmail.setText(it.email)
                    views.editDislayName.setText(it.name)
                    views.editDob.setText(it.birthday)
                    views.edtPass.setText("**************")
                    Glide.with(views.imgProfile).load(it.photoURL).centerCrop()
                        .error(getDrawable(R.drawable.ic_person))
                        .into(views.imgProfile)
                    id = it.id.toString()
                    user = it
                }
            }
        }
        views.toolbar.setOnClickListener { finishAfterTransition() }

        views.btnEdit.setOnClickListener {
            showButtonEdit(false)
        }

        views.imgChangePass.setSingleClickListener {
            views.edtPass.isEnabled = true
            DialogUtil.showAlertDialogAlert(this, cancelCallback = {
                views.edtPass.isEnabled = false
            }, confirmCallback = {
                views.send.show()
                views.imgChangePass.hide()
                views.edtPass.requestFocus()
                views.edtPass.setText("")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(views.edtPass, InputMethodManager.SHOW_IMPLICIT)
                val newPassword = views.edtPass.text


                views.send.setSingleClickListener {
                    views.loading.show()
                    if (newPassword.isNullOrEmpty()) views.edtPass.error =
                        R.string.password_not_empty.toString()
                    else {

                    }
                }
            })


        }


        views.btnSave.setOnClickListener {
            views.loading.show()
            showButtonEdit(true)
            if (views.editDislayName.text.isNullOrEmpty()) views.editDislayName.error =
                getString(R.string.dislay_name_not_Emty)
            else {
                val name = views.editDislayName.text.toString()
                val dob = views.editDob.text.toString()
                val data = checkChange(name, dob)
                if (!data.isNullOrEmpty()) {
                    authViewModel.handle(AuthViewAction.upDateUser(data))
                } else
                    views.loading.hide()
            }
        }
        views.btnCancel.setOnClickListener {
            showButtonEdit(true)
        }

        views.imgEdit.setOnClickListener {
            // Kiểm tra quyền trước khi truy cập Gallery.
            if (PermissionUtils.shouldAskForPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Yêu cầu quyền đọc bộ nhớ ngoài.
                PermissionUtils.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
                )
            } else {
                openGallery()
            }

        }
    }

    // Xử lý kết quả yêu cầu quyền từ người dùng.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, bạn có thể truy cập Gallery và chọn ảnh ở đây.
                openGallery()
            } else {
                // Quyền bị từ chối, hiển thị thông báo cho người dùng hoặc thực hiện xử lý phù hợp.
            }
        }
    }

    private fun openGallery() {
        // Mở Gallery để chọn ảnh và xử lý kết quả khi người dùng chọn ảnh.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(galleryIntent)
    }

    private fun registerResult() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // Xử lý kết quả ở đây.
                    try {
                        image = result.data?.data
                        views.imgProfile.setImageURI(image)

                        val imageStr=convertBitmapToBase64((views.imgProfile.drawable as BitmapDrawable).bitmap)
                        val imgUser = HashMap<String, Any>()
                        imageStr?.let {
                            imgUser["photoURL"] = it
                            authViewModel.handle(AuthViewAction.upDateUser(imgUser))
                            views.loading.show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


    private fun showButtonEdit(flag: Boolean) {
        if (flag) {
            views.btnEdit.show()
            views.btnSave.hide()
            views.btnCancel.hide()
            views.editDislayName.isEnabled = false
            views.editDob.isEnabled = false
        } else {
            views.btnEdit.hide()
            views.btnSave.show()
            views.btnCancel.show()
            views.editDislayName.isEnabled = true
            views.editDob.isEnabled = true
        }
    }

    private fun checkChange(name: String, dob: String): HashMap<String, Any> {
        if (name != user.name) {
            updateData["name"] = name
        }
        if (dob != user.birthday) {
            updateData["birthday"] = dob
        }
        return updateData
    }


    override fun getBinding(): ActivityAccountBinding {
        return ActivityAccountBinding.inflate(layoutInflater)
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}
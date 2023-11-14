package dev.son.movie.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityLogoutScreenBinding
import dev.son.movie.network.models.user.Category
import dev.son.movie.network.models.user.UserId
import dev.son.movie.network.models.user.ViewingHistory
import dev.son.movie.ui.login.LoginViewAction
import dev.son.movie.ui.login.LoginViewModel
import dev.son.movie.ui.login.LoginViewState
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import javax.inject.Inject

@Suppress("DEPRECATION")
class SignUpActivity : TrackingBaseActivity<ActivityLogoutScreenBinding>(), LoginViewModel.Factory {
    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private val loginViewModel: LoginViewModel by viewModel()
    private val userId: UserId = UserId()

    override fun getBinding(): ActivityLogoutScreenBinding {
        return ActivityLogoutScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setUpUi()
        loginViewModel.subscribe(this) {
            when (it.user) {
                is Success -> {
                    loginViewModel.handle(LoginViewAction.SaveDataUser(it.user.invoke()))
                    val intent = Intent(this, BottomNavActivity::class.java)
                    intent.putExtra("userId",it.user.invoke().userId)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    views.loading.hide()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        getString(R.string.registration_failed_please_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    views.loading.hide()
                }

                else -> {}
            }
        }
    }

    private fun setUpUi() {
        views.toolbar.setOnClickListener { finishAfterTransition() }
        views.signUpSubmit.setOnClickListener {
            views.loading.show()
            signUpSubmit()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun signUpSubmit() {
        var email = views.edtEmail.text.toString().trim()
        var password = views.password.text
        if (email.isNullOrEmpty()) views.emailTil.error = getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign Up Success", Toast.LENGTH_SHORT).show()
                        val user = FirebaseAuth.getInstance().currentUser
                        userId.apply {
                            this.email = user!!.email
                            this.userId = user.uid
                            this.avatar = ""
                            this.name = user.email?.substringBefore("@")
                            this.dateOfBirth = ""
                            this.coins=0
                        }
                        loginViewModel.handle(LoginViewAction.createUser(userId))
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            // Địa chỉ email đã tồn tại
                            Toast.makeText(
                                this,
                                "Email đã được đăng ký trước đó. Vui lòng sử dụng địa chỉ email khác.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", exception)
                            Toast.makeText(
                                this,
                                getString(R.string.registration_failed_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
        }
    }

    override fun create(initialState: LoginViewState): LoginViewModel {
        return loginViewModelFactory.create(initialState)
    }


}
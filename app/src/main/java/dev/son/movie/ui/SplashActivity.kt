package dev.son.movie.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import dev.son.movie.R

import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivitySplashBinding
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.ui.login.AuthViewAction
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.login.AuthViewState
import dev.son.movie.utils.setSingleClickListener
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>(), AuthViewModel.Factory {


    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory
    private val authViewModel: AuthViewModel by viewModel()
    private var token: String? = null

    @Inject
    lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        fetchData()
        resultData()
        views.button.setSingleClickListener {
//            if (currentUser != null) {
//                Intent(this, BottomNavActivity::class.java).also {
//                    it.putExtra("userId", currentUser.uid)
//                    startActivity(it)
//                    finish()
//                }
//            } else {
//                Intent(this, LoginActivity::class.java).also {
//                    startActivity(it)
//                    finish()
//                }
//            }
        }
    }

    private fun resultData() {
        authViewModel.subscribe(this) { it1 ->
            when (it1.currentUser) {
                is Success -> {
                    if (it1.currentUser.invoke().data.id != null) {
                        lifecycleScope.launch {
                            val isUserSaved =
                                userPreferences.saveUserData(it1.currentUser.invoke().data)
                            if (isUserSaved) {
                                val intent = Intent(this@SplashActivity, BottomNavActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                                finish()
                            } else {
                                // Xử lý khi lưu token không thành công
                                Toast.makeText(this@SplashActivity, "Vui Lòng thử lại", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Intent(this, LoginActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }

                is Fail -> {
                    Intent(this, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }

                else -> {}
            }
        }
    }

    private fun fetchData() {

        token = userPreferences.token

        if (token != null) {
            authViewModel.handle(AuthViewAction.getCurrentUser)
        } else {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

    }

    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}
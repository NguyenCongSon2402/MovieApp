package dev.son.movie.ui.login

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
import dev.son.movie.databinding.ActivityLoginScreenBinding
import dev.son.movie.network.models.user.LoginRequest
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.ForgotPasswordActivity
import dev.son.movie.ui.SignUpActivity
import dev.son.movie.utils.hide
import dev.son.movie.utils.setSingleClickListener
import dev.son.movie.utils.show
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginScreenBinding>(), AuthViewModel.Factory {
    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory
    private val authViewModel: AuthViewModel by viewModel()

    @Inject
    lateinit var userPreferences: UserPreferences
    override fun getBinding(): ActivityLoginScreenBinding {
        return ActivityLoginScreenBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setUpUi()
        authViewModel.subscribe(this) {
            when (it.tokenResponse) {
                is Success -> {
                    views.loading.hide()
                    lifecycleScope.launch {
                        val isTokenSaved =
                            userPreferences.saveToken(it.tokenResponse.invoke().accessToken.toString())
                        val isUserSaved = it.tokenResponse.invoke().data?.let { it1 ->
                            userPreferences.saveUserData(
                                it1
                            )
                        }
                        if (isTokenSaved && isUserSaved == true) {
                            // Token đã được lưu thành công
                            val intent = Intent(this@LoginActivity, BottomNavActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                            finish()
                        } else {
                            // Xử lý khi lưu token không thành công
                            Toast.makeText(
                                this@LoginActivity,
                                "Lưu token không thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

                is Fail -> {
                    views.loading.hide()
                    Toast.makeText(this, "Sign in fail", Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    private fun setUpUi() {
        views.loginSubmit.setOnClickListener {
            views.loading.show()
            loginSubmit()
        }
        views.txtForgot.setSingleClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        views.signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun loginSubmit() {
        var email = views.edtEmail.text.toString().trim()
        var password = views.password.text.toString()
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val loginRequest = LoginRequest(email = email, password = password)

            authViewModel.handle(AuthViewAction.auth(loginRequest))
        }
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}
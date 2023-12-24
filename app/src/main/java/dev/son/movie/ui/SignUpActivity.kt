package dev.son.movie.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityLogoutScreenBinding
import dev.son.movie.network.models.user.UserId
import dev.son.movie.ui.login.AuthViewAction
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.login.AuthViewState
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import javax.inject.Inject

@Suppress("DEPRECATION")
class SignUpActivity : TrackingBaseActivity<ActivityLogoutScreenBinding>(), AuthViewModel.Factory {
    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory
    private val authViewModel: AuthViewModel by viewModel()
    private val userId: UserId = UserId()

    override fun getBinding(): ActivityLogoutScreenBinding {
        return ActivityLogoutScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setUpUi()
        authViewModel.subscribe(this) {
            when (it.user) {
                is Success -> {
                    authViewModel.handle(AuthViewAction.SaveDataUser(it.user.invoke()))
                    val intent = Intent(this, BottomNavActivity::class.java)
                    intent.putExtra("userId", it.user.invoke().userId)
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
        }
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}
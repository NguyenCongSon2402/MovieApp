package dev.son.movie.ui

import android.os.Bundle
import dev.son.movie.R
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityLogoutScreenBinding

@Suppress("DEPRECATION")
class SignUpActivity : TrackingBaseActivity<ActivityLogoutScreenBinding>() {
    override fun getBinding(): ActivityLogoutScreenBinding {
        return ActivityLogoutScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpUi()
    }

    private fun setUpUi() {
        views.toolbar.setOnClickListener { finishAfterTransition() }
        views.signUpSubmit.setOnClickListener {
            signUpSubmit()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun signUpSubmit() {
        var email = views.edtEmail.toString().trim()
        var password = views.password.text
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {

        }
    }


}
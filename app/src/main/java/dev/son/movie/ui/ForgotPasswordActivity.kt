package dev.son.movie.ui

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dev.son.movie.R
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityForgotPassBinding
import dev.son.movie.utils.hide
import dev.son.movie.utils.show


class ForgotPasswordActivity : TrackingBaseActivity<ActivityForgotPassBinding>() {
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

    private fun signUpSubmit() {
        views.loading.show()
        val auth = FirebaseAuth.getInstance()
        val email = views.edtEmail.text
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.email_is_empty)
        else {
            auth.sendPasswordResetEmail(email.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Hãy kiểm tra email để đặt lại mật khẩu.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        views.loading.hide()
                    } else {
                        Toast.makeText(
                            this,
                            "Gửi thất bại. Vui lòng thử lại sau!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        views.loading.hide()
                    }
                }
        }
    }

    override fun getBinding(): ActivityForgotPassBinding {
        return ActivityForgotPassBinding.inflate(layoutInflater)
    }
}
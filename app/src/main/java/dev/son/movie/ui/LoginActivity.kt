package dev.son.movie.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dev.son.movie.R
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityLoginScreenBinding

class LoginActivity : TrackingBaseActivity<ActivityLoginScreenBinding>() {

    override fun getBinding(): ActivityLoginScreenBinding {
        return ActivityLoginScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setUpUi()
    }

    private fun setUpUi() {
        views.loginSubmit.setOnClickListener {
            loginSubmit()
        }
        views.signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun loginSubmit() {
        var email = views.edtEmail.toString().trim()
        var password = views.password.text
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
    }
}
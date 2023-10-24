package dev.son.movie.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.airbnb.mvrx.viewModel
import com.google.firebase.auth.FirebaseAuth
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivityLoginScreenBinding
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.SignUpActivity
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginScreenBinding>(), LoginViewModel.Factory {
    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private val loginViewModel: LoginViewModel by viewModel()
    override fun getBinding(): ActivityLoginScreenBinding {
        return ActivityLoginScreenBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            Intent(this, BottomNavActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
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
        var email = views.edtEmail.text.toString().trim()
        var password = views.password.text.toString()
        if (email.isNullOrEmpty()) views.emailTil.error =
            getString(R.string.username_not_empty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, BottomNavActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
    }

    override fun create(initialState: LoginViewState): LoginViewModel {
        return loginViewModelFactory.create(initialState)
    }
}
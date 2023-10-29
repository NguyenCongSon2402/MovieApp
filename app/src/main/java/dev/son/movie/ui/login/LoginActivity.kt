package dev.son.movie.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.google.firebase.auth.FirebaseAuth
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityLoginScreenBinding
import dev.son.movie.network.models.user.UserId
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.SignUpActivity
import javax.inject.Inject

class LoginActivity : TrackingBaseActivity<ActivityLoginScreenBinding>(), LoginViewModel.Factory {
    @Inject
    lateinit var loginViewModelFactory: LoginViewModel.Factory
    private val loginViewModel: LoginViewModel by viewModel()

    @Inject
    lateinit var userPreferences: UserPreferences
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
        loginViewModel.subscribe(this) {
            when (it.dataUser) {
                is Success -> {
                    loginViewModel.handle(LoginViewAction.SaveDataUser(it.dataUser.invoke()))
                    views.loading.visibility = View.GONE
                    val intent = Intent(this, BottomNavActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                }
                is Fail -> {
                    views.loading.visibility = View.GONE
                    Toast.makeText(this, "Sign in fail", Toast.LENGTH_SHORT).show()
                }

                is Loading -> {
                    views.loading.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
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

                        loginViewModel.handle(LoginViewAction.getUser(task.result.user!!.uid))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
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
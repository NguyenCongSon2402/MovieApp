package dev.son.movie.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import dev.son.movie.R
import dev.son.movie.TrackingApplication
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.data.local.UserPreferences
import dev.son.movie.databinding.ActivityLogoutScreenBinding
import dev.son.movie.network.models.Register
import dev.son.movie.network.models.user.UserId
import dev.son.movie.ui.login.AuthViewAction
import dev.son.movie.ui.login.AuthViewModel
import dev.son.movie.ui.login.AuthViewState
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.utils.hide
import dev.son.movie.utils.show
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@Suppress("DEPRECATION")
class SignUpActivity : TrackingBaseActivity<ActivityLogoutScreenBinding>(), AuthViewModel.Factory {
    @Inject
    lateinit var authViewModelFactory: AuthViewModel.Factory
    private val authViewModel: AuthViewModel by viewModel()
    private val userId: UserId = UserId()
    private var current = ""
    private val ddmmyyyy = "DDMMYYYY"
    private val cal: Calendar = Calendar.getInstance()
    private var dob: String? = null
    private var email: String? = null
    private var name: String? = null
    private var password: String? = null

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun getBinding(): ActivityLogoutScreenBinding {
        return ActivityLogoutScreenBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)
        setUpUi()
        authViewModel.subscribe(this) {
            when (it.register) {
                is Success -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    views.loading.hide()
                    Toast.makeText(
                        this@SignUpActivity,
                        getString(R.string.sign_up_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    authViewModel.handleRemoveRegister()
                }

                is Fail -> {
                    Toast.makeText(
                        this,
                        getString(R.string.registration_failed_please_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    views.loading.hide()
                    authViewModel.handleRemoveRegister()
                    views.signUpSubmit.isEnabled = true
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
        views.editDob.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().equals(current)) {
                    var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]".toRegex(), "")

                    val cl = clean.length
                    var sel = cl
                    for (i in 2..cl step 2) {
                        sel++
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean += ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var day = clean.substring(0, 2).toInt()
                        var mon = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()

                        mon = if (mon > 12) 12 else mon
                        cal[Calendar.MONTH] = mon - 1

                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal[Calendar.YEAR] = year
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        val maxDay = cal.getActualMaximum(Calendar.DATE)
                        day = if (day > maxDay) maxDay else day
                        clean = String.format(Locale.US, "%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format(
                        Locale.US, "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    views.editDob.setText(current)
                    views.editDob.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun signUpSubmit() {
        email = views.edtEmail.text.toString().trim()
        name = views.editDislayName.text.toString().trim()
        password = views.password.text.toString()
        dob = views.editDob.text.toString()
        if (name.isNullOrEmpty()) views.nameTil.error = getString(R.string.username_not_empty)
        if (email.isNullOrEmpty()) views.emailTil.error = getString(R.string.email_is_empty)
        if (dob.isNullOrEmpty()) views.dobTil.error = getString(R.string.dob_is_not_emty)
        if (password.isNullOrEmpty()) views.passwordTil.error =
            getString(R.string.password_not_empty)
        if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && !name.isNullOrEmpty() && !dob.isNullOrEmpty()) {
            val registrationInfo = Register(
                name = name!!,
                password = password!!,
                email = email!!,
                birthday = dob!!
            )
            authViewModel.handle(AuthViewAction.register(registrationInfo))
            views.signUpSubmit.isEnabled = false
        }
    }

    override fun create(initialState: AuthViewState): AuthViewModel {
        return authViewModelFactory.create(initialState)
    }
}
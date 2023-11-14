package dev.son.movie.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import dev.son.movie.R
import dev.son.movie.core.TrackingBaseActivity
import dev.son.movie.databinding.ActivitySplashBinding
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.utils.setSingleClickListener

class SplashActivity : TrackingBaseActivity<ActivitySplashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        views.button.setSingleClickListener {
            if (currentUser != null) {
                Intent(this, BottomNavActivity::class.java).also {
                    it.putExtra("userId",currentUser.uid)
                    startActivity(it)
                    finish()
                }
            } else {
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }
}
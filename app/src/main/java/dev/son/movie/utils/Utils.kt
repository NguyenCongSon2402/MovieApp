package dev.son.movie.utils

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Fail
import dev.son.movie.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Date.format(format: String? = null): String {
    val ld = toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    return ld.format(DateTimeFormatter.ofPattern(format ?: "dd/MM/yyyy"))
}

fun AppCompatActivity.addFragment(
    frameId: Int,
    fragment: Fragment,
    allowStateLoss: Boolean = false
) {
    supportFragmentManager.commitTransaction(allowStateLoss) { add(frameId, fragment) }
}

inline fun androidx.fragment.app.FragmentManager.commitTransaction(
    allowStateLoss: Boolean = false,
    func: FragmentTransaction.() -> FragmentTransaction
) {
    val transaction = beginTransaction().func()
    if (allowStateLoss) {
        transaction.commitAllowingStateLoss()
    } else {
        transaction.commit()
    }
}

fun <T : Fragment> AppCompatActivity.addFragmentToBackstack(
    frameId: Int,
    fragmentClass: Class<T>,
    tag: String? = null,
    allowStateLoss: Boolean = false,
    option: ((FragmentTransaction) -> Unit)? = null
) {
    supportFragmentManager.commitTransaction(allowStateLoss) {
        option?.invoke(this)
        replace(frameId, fragmentClass, null, tag).addToBackStack(tag)
    }
}

fun extractVideoIdFromUrl(url: String): String? {
    val pattern = Pattern.compile("watch\\?v=([a-zA-Z0-9_-]+)")
    val matcher = pattern.matcher(url)
    if (matcher.find()) {
        return matcher.group(1)
    }
    return null
}

fun <T> checkStatusApiRes(err: Fail<T>): Int {
    return when (err.error.message!!.trim()) {
        "HTTP 200" -> {
            R.string.http200
        }

        "HTTP 401" -> {
            R.string.http401
        }

        "HTTP 403" -> {
            R.string.http403
        }

        "HTTP 404" -> {
            R.string.http404
        }

        "HTTP 500" -> {
            R.string.http500
        }

        else -> {
            R.string.http500
        }
    }
}
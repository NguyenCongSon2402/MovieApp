@file:Suppress("DEPRECATION")

package dev.son.movie.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Fail
import com.bumptech.glide.Glide
import dev.son.movie.R
import dev.son.movie.network.models.movie.Genre
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

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


fun convertStringToFormattedDate(dateString: String): String {
    val instant = Instant.parse(dateString)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")
    return localDateTime.format(formatter)
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

fun showDownloadConfirmationDialog(context: Context, onDownloadConfirmed: () -> Unit) {
    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.setMessage("Bạn có chắc chắn muốn huỷ?")
    dialogBuilder.setPositiveButton("Đồng ý") { _, _ ->
        // Xử lý tải
        onDownloadConfirmed.invoke()
    }
    dialogBuilder.setNegativeButton("Huỷ") { dialog, _ ->
        dialog.dismiss()
    }
    val dialog = dialogBuilder.create()
    dialog.show()
}
fun getNamesByCodes(codes: String): String {
    val names = codes.split(", ")
        .map { it.trim() }
        .filter { !it.contains("@") }
        .mapNotNull { getNameByCode(it) ?:"" }
    return names.joinToString("-")
}
fun getListNamesByCodes(codes: String): List<String> {
    val names = codes.split(", ")
        .map { it.trim() }
        .filter { !it.contains("@") }
        .mapNotNull { getNameByCode(it) ?:"" }
    return names
}
fun getListCodesByCodes(codes: String): List<String> {
    val names = codes.split(", ")
        .map { it.trim() }
        .filter { !it.contains("@") }
    return names
}
fun getNameByCode(code: String): String? {
    val genres: Array<Genre> = arrayOf(
        Genre("Hành Động", "action"),
        Genre("Tình Cảm", "romance"),
        Genre("Hài Hước", "comedy"),
        Genre("Cổ Trang", "historical"),
        Genre("Tâm Lý", "drama"),
        Genre("Hình Sự", "crime"),
        Genre("Chiến Tranh", "war"),
        Genre("Thể Thao", "sports"),
        Genre("Võ Thuật", "martial_arts"),
        Genre("Viễn Tưởng", "sci_fi"),
        Genre("Phiêu Lưu", "adventure"),
        Genre("Khoa Học", "science"),
        Genre("Kinh Dị", "horror"),
        Genre("Âm Nhạc", "music"),
        Genre("Thần Thoại", "mythology"),
        Genre("Tài Liệu", "documentary"),
        Genre("Gia Đình", "family"),
        Genre("Chính kịch", "drama"),
        Genre("Bí ẩn", "mystery"),
        Genre("Học Đường", "school"),
        Genre("Kinh Điển", "classic"),
        Genre("Phim 18+", "adult"),
        Genre("Phim Bộ", "@series"),
        Genre("Phim Lẻ", "@single"),
        Genre("Phim Hoạt Hình", "@hoathinh"),
        Genre("TV Show", "@tvshows"),
        Genre("Phim VietSub", "@vietSub"),
        Genre("Phim Thuyết Minh", "@thuyetminh"),
        Genre("Phim Bộ Đang Chiếu", "@dangchieu"),
        Genre("Phim Bộ Đã Hoàn Thành", "@hoanthanh")
    )
    val genre = genres.find { it.code == code }
    return genre?.name ?: ""
}
fun parseTitle(inputString: String): String {
    val regex = Regex("""Tập (\d+)? (.+)""")
    val matchResult = regex.matchEntire(inputString)

    return matchResult?.groups?.get(2)?.value ?: inputString
}
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
 fun convertBitmapToBase64(bitmap: Bitmap): String? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
    return "data:image/png;base64,$base64Image"
}
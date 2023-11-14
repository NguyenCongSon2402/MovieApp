@file:Suppress("DEPRECATION")

package dev.son.movie.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager


object PermissionUtils {
    // Phương thức kiểm tra xem phiên bản Android hiện tại có hỗ trợ kiểm tra runtime permission hay không.
    fun useRunTimePermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    // Phương thức kiểm tra xem ứng dụng đã được cấp quyền sử dụng một quyền cụ thể chưa.
    fun hasPermission(activity: Activity, permission: String?): Boolean {
        return if (useRunTimePermissions()) {
            activity.checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
        } else true
        // Trên các phiên bản Android cũ hơn không cần kiểm tra runtime permission.
    }

    // Phương thức yêu cầu cấp quyền sử dụng một hoặc nhiều quyền.
    fun requestPermissions(activity: Activity, permissions: Array<String?>, requestCode: Int) {
        if (useRunTimePermissions()) {
            activity.requestPermissions(permissions, requestCode)
        }
    }

    // Phương thức yêu cầu cấp quyền sử dụng một hoặc nhiều quyền từ Fragment.
    fun requestPermissions(fragment: Fragment, permissions: Array<String?>, requestCode: Int) {
        if (useRunTimePermissions()) {
            fragment.requestPermissions(permissions, requestCode)
        }
    }

    // Phương thức kiểm tra xem người dùng đã từng từ chối việc cấp quyền trước đây hay không.
    fun shouldShowRationale(activity: Activity, permission: String?): Boolean {
        return if (useRunTimePermissions()) {
            activity.shouldShowRequestPermissionRationale(permission!!)
        } else false
        // Trên các phiên bản Android cũ hơn không cần hiển thị lời giải thích.
    }

    // Phương thức kiểm tra xem ứng dụng có nên yêu cầu cấp quyền từ người dùng hay không.
    fun shouldAskForPermission(activity: Activity, permission: String?): Boolean {
        return if (useRunTimePermissions()) {
            !hasPermission(activity, permission) &&
                    (!hasAskedForPermission(activity, permission) ||
                            shouldShowRationale(activity, permission))
        } else false
        // Trên các phiên bản Android cũ hơn không cần kiểm tra runtime permission.
    }

    // Phương thức chuyển người dùng đến cài đặt ứng dụng để cấp quyền nếu họ từng từ chối.
    fun goToAppSettings(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    // Phương thức kiểm tra xem người dùng đã từng được hỏi về việc cấp quyền hay chưa.
    fun hasAskedForPermission(activity: Activity, permission: String?): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(activity)
            .getBoolean(permission, false)
    }

    // Phương thức đánh dấu rằng người dùng đã được hỏi về việc cấp quyền.
    fun markPermissionAsAsked(activity: Activity, permission: String?) {
        PreferenceManager
            .getDefaultSharedPreferences(activity)
            .edit()
            .putBoolean(permission, true)
            .apply()
    }
}

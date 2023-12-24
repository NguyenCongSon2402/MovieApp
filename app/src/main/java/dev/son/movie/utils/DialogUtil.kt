package dev.son.movie.utils

import android.app.Dialog
import android.content.Context
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import dev.son.movie.R


object DialogUtil {


    fun showAlertDialogSuccess(context: Context, heading: String, description: String) {
        PopupDialog.getInstance(context)
            .setStyle(Styles.SUCCESS)
            .setHeading(heading)
            .setDescription(
                description
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onDismissClicked(dialog: Dialog) {
                    super.onDismissClicked(dialog)
                }
            })
    }

    fun showAlertDialogAlert(
        context: Context,
        confirmCallback: () -> Unit,
        cancelCallback: () -> Unit    ) {
        PopupDialog.getInstance(context)
            .setStyle(Styles.STANDARD)
            .setHeading("Change password!")
            .setDescription(
                "Are you sure you want to change the password"
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onPositiveClicked(dialog: Dialog) {
                    super.onPositiveClicked(dialog)
                    confirmCallback.invoke()
                }

                override fun onNegativeClicked(dialog: Dialog) {
                    super.onNegativeClicked(dialog)
                    cancelCallback.invoke()
                }
            })
    }
    fun showAlertDialogLogOut(
        context: Context,
        confirmCallback: () -> Unit) {
        PopupDialog.getInstance(context)
            .setStyle(Styles.IOS)
            .setHeading("Logout")
            .setDescription(
                "Are you sure you want to logout?" +
                        " This action cannot be undone"
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onPositiveClicked(dialog: Dialog) {
                    super.onPositiveClicked(dialog)
                    confirmCallback.invoke()
                }

                override fun onNegativeClicked(dialog: Dialog) {
                    super.onNegativeClicked(dialog)
                }
            })
    }

    fun showDialogDelete(context: Context, confirmCallback: () -> Unit){
        PopupDialog.getInstance(context)
            .setStyle(Styles.ANDROID_DEFAULT)
            .setHeading("Delete")
            .setHeadingTextColor(R.color.red)
            .setDescription(
                "Are you sure you want to delete?" +
                        " This action cannot be undone"
            )
            .setCancelable(false)
            .showDialog(object : OnDialogButtonClickListener() {
                override fun onPositiveClicked(dialog: Dialog) {
                    super.onPositiveClicked(dialog)
                    confirmCallback.invoke()
                }

                override fun onNegativeClicked(dialog: Dialog) {
                    super.onNegativeClicked(dialog)
                }
            })
    }

}
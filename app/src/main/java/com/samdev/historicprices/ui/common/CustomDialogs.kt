package com.samdev.historicprices.ui.common

import android.app.AlertDialog
import android.content.Context

/**
 * @author Sam
 */
object CustomDialogs {

    fun showErrorDialog(
        context: Context,
        msg: String,
        retry: () -> Unit,
    ) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle("Error")
        alertDialog.setMessage(msg)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Retry"
        ) { dialog, _ ->
            retry.invoke()
            dialog.dismiss()
        }
        alertDialog.show()
    }
}
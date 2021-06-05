package com.jschoi.develop.opgg.util

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast

object Util {

    fun showToastMessage(context: Context, message: String?) {
        Toast.makeText(context, message ?: "통신 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
    }

    fun showDialogMessage(context: Context, message: String?) {
        AlertDialog.Builder(context)
                .setTitle("Alert Message")
                .setMessage(message ?: "통신 중 오류가 발생하였습니다.")
                .setPositiveButton("확인") { _, _ -> Unit }
                .show()
    }
}
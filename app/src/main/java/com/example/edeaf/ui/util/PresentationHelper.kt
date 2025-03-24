package com.example.edeaf.ui.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.edeaf.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PresentationHelper
{
    fun Context.showMaterialDialog(
        show: Boolean = false,
        message: String = ""
    ) {
        if (show) {
           val dialog = MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .show()

            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
            }, 1000)
        }
    }
}
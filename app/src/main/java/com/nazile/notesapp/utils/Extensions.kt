package com.nazile.notesapp.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.nazile.notesapp.R


fun Context.showSnackBar(message: String?, type: String) {
    if (type == "success") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.toast(message: String): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }
fun Activity.openUrl(url: String) {
    val uri = url.toUri()
    val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(myAppLinkToMarket)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            this,
            "No client found",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun Activity.shareApp() {
    val shareApp = Intent(Intent.ACTION_SEND)
    shareApp.type = "text/plain"
    shareApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
    shareApp.putExtra(
        Intent.EXTRA_TEXT,
        """
                    ${resources.getString(R.string.app_name)}:
                    https://play.google.com/store/apps/details?id=${this.packageName}
                    """.trimIndent()
    )
    startActivity(shareApp)
}

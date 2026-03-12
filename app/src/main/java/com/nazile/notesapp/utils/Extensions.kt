package com.nazile.notesapp.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast


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


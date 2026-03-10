package com.nazile.notesapp.utils

import android.content.Context
import android.widget.Toast


fun Context.showSnackBar(message: String?, type: String) {
    if (type == "success") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

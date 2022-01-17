package com.enozom.poc.e_invoice.utils.extensions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context?.checkCameraPermission(): Boolean {
    if (this != null) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
    }
    return false
}
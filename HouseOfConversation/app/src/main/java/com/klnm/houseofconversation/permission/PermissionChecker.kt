package com.klnm.houseofconversation.permission

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

class PermissionChecker(private val context : Context) {

    fun lackPermission(vararg permissions: String): Boolean {
        for(permission in permissions) {
            if (lacksPermission(permission)) {
                return true
            }
        }
        return false
    }

    private fun lacksPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED
    }
}
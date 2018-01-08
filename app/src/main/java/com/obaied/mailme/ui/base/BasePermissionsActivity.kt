package com.joseph.mailme.ui.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by ab on 12.10.17.
 */
abstract class BasePermissionsActivity : BaseActivity() {
    companion object {
        val STATE_IN_PERMISSION = "state_in_permission"
        val REQUEST_PERMISSIONS = 61111
    }

    private var state: Bundle? = null
    private var isInPermission: Boolean = false
    internal var didGrantPermissions: Boolean = false
        private set

    abstract protected fun getDesiredPermissions(): Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        state = savedInstanceState
        state?.let { isInPermission = it.getBoolean(STATE_IN_PERMISSION) }

        if (hasAllPermissions(getDesiredPermissions())) {
            onPermissionsGranted()
        } else if (!isInPermission) {
            isInPermission = true

            ActivityCompat
                    .requestPermissions(this,
                            netPermissions(getDesiredPermissions()),
                            REQUEST_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        isInPermission = false

        if (requestCode == REQUEST_PERMISSIONS) {
            if (hasAllPermissions(getDesiredPermissions())) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(STATE_IN_PERMISSION, isInPermission)
    }

    protected open fun onPermissionsGranted() {
        didGrantPermissions = true
    }

    protected open fun onPermissionsDenied() {
        didGrantPermissions = false
    }

    private fun hasAllPermissions(permissions: Array<String>): Boolean =
            permissions.any { hasPermission(it) }

    private fun hasPermission(permission: String): Boolean =
            ContextCompat.
                    checkSelfPermission(this,
                            permission) == PackageManager.PERMISSION_GRANTED

    private fun netPermissions(wanted: Array<String>): Array<String> {
        val result = arrayListOf<String>()

        wanted.forEach { if (!hasPermission(it)) result.add(it) }
        return result.toTypedArray()
    }
}
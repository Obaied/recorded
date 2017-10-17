package com.obaied.mailme.ui.base

/**
 * Created by ab on 12.10.17.
 */
open class BasePermissionsFragment : BaseFragment() {
    fun didGrantPermissions(): Boolean {
        val permissionsActivity = activity
        if (permissionsActivity is BasePermissionsActivity) {
            return permissionsActivity.didGrantPermissions
        }

        return false
    }
}
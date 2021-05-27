package org.tumba.kegel_app.core.system

import android.content.Context
import android.provider.Settings
import javax.inject.Inject

interface PermissionProvider {

    fun canDrawOverlays(): Boolean
}

class PermissionProviderImpl @Inject constructor(
    private val context: Context
) : PermissionProvider{

    override fun canDrawOverlays(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
}
package org.tumba.kegel_app.core.system

import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

interface ResourceProvider {

    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getColor(@ColorRes resId: Int): Int
}

class ResourceProviderImpl(
    private val resources: Resources
) : ResourceProvider {

    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return resources.getString(resId, *args)
    }

    override fun getColor(resId: Int): Int {
        return ResourcesCompat.getColor(resources, resId, null)
    }
}
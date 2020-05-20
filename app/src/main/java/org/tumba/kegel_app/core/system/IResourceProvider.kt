package org.tumba.kegel_app.core.system

import android.content.res.Resources
import androidx.annotation.StringRes

interface IResourceProvider {

    fun getString(@StringRes resId: Int, vararg args: Any): String
}

class ResourceProviderImpl(
    private val resources: Resources
) : IResourceProvider {

    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return resources.getString(resId, args)
    }
}
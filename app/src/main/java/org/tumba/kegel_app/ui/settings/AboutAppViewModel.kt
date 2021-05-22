package org.tumba.kegel_app.ui.settings

import org.tumba.kegel_app.BuildConfig
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class AboutAppViewModel @Inject constructor(
) : BaseViewModel() {

    val version = BuildConfig.VERSION_NAME


    fun onPrivacyPolicyClicked() {
        navigate(AboutAppFragmentDirections.actionGlobalExternalPrivacyPolicy())
    }

    fun onTermOfUsageClicked() {
        navigate(AboutAppFragmentDirections.actionGlobalExternalTermOfUsage())
    }
}

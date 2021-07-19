package org.tumba.kegel_app.ui.home.dialog

import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.HomeScreenTracker
import org.tumba.kegel_app.billing.ProUpgradeManager
import org.tumba.kegel_app.config.ConfigConstants
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.showSnackbar
import javax.inject.Inject

class FreePeriodSuggestionViewModel @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val proUpgradeManager: ProUpgradeManager,
    private val tracker: HomeScreenTracker,
    remoteConfig: FirebaseRemoteConfig
) : BaseViewModel() {

    val freePeriodDays = remoteConfig[ConfigConstants.defaultFreePeriodDays].asLong().toInt()

    init {
        tracker.trackFreePeriodSuggestionShown()
    }

    fun onClickYes() {
        tracker.trackFreePeriodSuggestionOkClicked()
        viewModelScope.launch { activateFreePeriod() }
    }

    fun onClickCancel() {
        tracker.trackFreePeriodSuggestionCancelClicked()
    }

    private fun activateFreePeriod() {
        viewModelScope.launch {
            proUpgradeManager.activateDefaultFreePeriod()
            showSnackbar(
                resourceProvider.getString(
                    R.string.screen_pro_upgrade_message_free_period_activated,
                    freePeriodDays.toString()
                )
            )
            back()
        }
    }
}

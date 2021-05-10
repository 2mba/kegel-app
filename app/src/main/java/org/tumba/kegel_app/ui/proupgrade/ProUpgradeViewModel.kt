package org.tumba.kegel_app.ui.proupgrade

import org.tumba.kegel_app.analytics.SettingsTracker
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class ProUpgradeViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor,
    private val resourceProvider: ResourceProvider,
    private val tracker: SettingsTracker
) : BaseViewModel() {


}


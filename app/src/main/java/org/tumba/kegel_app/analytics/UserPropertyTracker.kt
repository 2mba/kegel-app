package org.tumba.kegel_app.analytics

import javax.inject.Inject

class UserPropertyTracker @Inject constructor(private val analytics: Analytics) : TrackerScope {

    fun setProPurchased(isProPurchased: Boolean) {
        analytics.setUserProperty("pro_purchased", isProPurchased.toString())
    }
}

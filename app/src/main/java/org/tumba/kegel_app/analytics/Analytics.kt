package org.tumba.kegel_app.analytics

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.analytics.FirebaseAnalytics as OriginFirebaseAnalytics

interface Analytics {

    fun trackScreen(screen: String, args: Bundle? = null)

    fun track(eventName: String, screenName: String? = null, parameters: Bundle? = null)

    fun setUserProperty(name: String, value: String)
}

class FirebaseAnalytics @Inject constructor() : Analytics {

    private val firebase = Firebase.analytics

    override fun trackScreen(screen: String, args: Bundle?) {
        firebase.logEvent(OriginFirebaseAnalytics.Event.SCREEN_VIEW) {
            param(OriginFirebaseAnalytics.Param.SCREEN_NAME, screen)
            if (args != null) {
                param(KEY_SCREEN_ARGS, args)
            }
        }
    }

    override fun track(eventName: String, screenName: String?, parameters: Bundle?) {
        val parametersNotNull = parameters ?: Bundle()
        if (screenName != null) {
            parametersNotNull.putString(OriginFirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
        firebase.logEvent(eventName, parameters)
        Timber.e("!!!!! logEvent -> $eventName")
    }

    override fun setUserProperty(name: String, value: String) {
        firebase.setUserProperty(name, value)
    }

    companion object {
        private const val KEY_SCREEN_ARGS = "Arguments"
    }
}

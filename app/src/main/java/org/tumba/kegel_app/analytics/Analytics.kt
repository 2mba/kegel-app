package org.tumba.kegel_app.analytics

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import com.google.firebase.analytics.FirebaseAnalytics as OriginFirebaseAnalytics

interface Analytics {

    fun trackScreen(screen: String, args: Bundle? = null)
}

class FirebaseAnalytics @Inject constructor(): Analytics {

    private val firebase = Firebase.analytics

    override fun trackScreen(screen: String, args: Bundle?) {
        firebase.logEvent(OriginFirebaseAnalytics.Event.SCREEN_VIEW) {
            param(OriginFirebaseAnalytics.Param.SCREEN_NAME, screen)
            if (args != null) {
                param(KEY_SCREEN_ARGS, args)
            }
        }
    }

    companion object {
        private const val KEY_SCREEN_ARGS = "Arguments"
    }
}

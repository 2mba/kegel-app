package org.tumba.kegel_app.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import timber.log.Timber
import javax.inject.Inject

interface RemoteConfigFetcher {

    fun fetchAndActivate()
}

class RemoteConfigFetcherImpl @Inject constructor() : RemoteConfigFetcher {

    @Suppress("MaxLineLength")
    override fun fetchAndActivate() {
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            Timber.d(
                task.exception,
                if (task.isSuccessful) {
                    "Remote config fetched and activated successfully: ${Firebase.remoteConfig.info.lastFetchStatus}"
                } else {
                    "Remote config fetching failed"
                }
            )
            logConfig()
        }
    }

    private fun logConfig() {
        Timber.d("!!! Firebase config -> ${Firebase.remoteConfig.all.map { it.key to it.value.asString() }.toMap()}")
    }
}

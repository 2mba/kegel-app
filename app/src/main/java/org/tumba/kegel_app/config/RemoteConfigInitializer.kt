package org.tumba.kegel_app.config

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.tumba.kegel_app.BuildConfig
import org.tumba.kegel_app.R
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes

@OptIn(ExperimentalTime::class)
class RemoteConfigInitializer @Inject constructor() {

    private val devMinimumFetchInterval = 1.minutes
    private val defaultMinimumFetchInterval = 1.hours

    fun init() {
        Firebase.remoteConfig.apply {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = getMinimumFetchInterval()
            }
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    private fun getMinimumFetchInterval(): Long {
        val interval = if (BuildConfig.DEBUG) {
            devMinimumFetchInterval
        } else {
            defaultMinimumFetchInterval
        }
        return interval.inSeconds.toLong()
    }
}

package org.tumba.kegel_app.core.system

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

interface NightModeManager {

    fun setNightMode(mode: Int)

    fun getNightMode(): Int
}

class NightModeManagerImpl @Inject constructor(): NightModeManager {

    override fun setNightMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun getNightMode(): Int = AppCompatDelegate.getDefaultNightMode()
}
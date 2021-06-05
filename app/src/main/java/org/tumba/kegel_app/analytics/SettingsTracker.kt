package org.tumba.kegel_app.analytics

import androidx.core.os.bundleOf
import javax.inject.Inject

class SettingsTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "Settings")

    fun trackReminderEnabledChanged(enabled: Boolean) {
        tracker.track("reminder_enabled", bundleOf("enabled" to enabled))
    }

    fun trackReminderDayChanged(day: Int, enabled: Boolean, days: List<Boolean>) {
        tracker.track(
            "reminder_day_changed",
            bundleOf(
                "day" to day,
                "enabled" to enabled
            ).apply {
                days.forEachIndexed { day, enabled -> putBoolean("day${day}", enabled) }
            }
        )
    }

    fun trackReminderTimeChanged(hour: Int, minute: Int) {
        tracker.track(
            "reminder_time_changed",
            bundleOf(
                "hour" to hour,
                "minute" to minute
            )
        )
    }

    fun trackSetCustomLevelClicked() {
        tracker.track(click("set_custom_level"))
    }

    fun trackCustomLevelSelected(level: Int) {
        tracker.track(
            click("custom_level_selected"),
            bundleOf("level" to level)
        )
    }

    fun trackRateAppClicked() {
        tracker.track(click("rate_app"))
    }

    fun trackNightModeClicked() {
        tracker.track(click("night_mode"))
    }

    fun trackNightModeSelected() {
        tracker.track("night_mode_selected")
    }

    fun trackSoundVolumeClicked() {
        tracker.track(click("sound_volume"))
    }

    fun trackSoundVolumeSelected() {
        tracker.track("sound_volume_selected")
    }

    fun trackSoundPackClicked() {
        tracker.track(click("sound_pack"))
    }

    fun trackSoundPackSelected(id: Int) {
        tracker.track("sound_pack_selected", bundleOf("id" to id))
    }

    fun trackNavigateToProUpgradeFromButton() {
        tracker.track("navigate_to_pro_upgrade_stng_button")
    }

    fun trackNavigateToProUpgradeFromSoundVolume() {
        tracker.track("navigate_to_pro_upgrade_stng_snd_volume")
    }

    fun trackNavigateToProUpgradeFromSoundPack() {
        tracker.track("navigate_to_pro_upgrade_stng_snd_pack")
    }
}

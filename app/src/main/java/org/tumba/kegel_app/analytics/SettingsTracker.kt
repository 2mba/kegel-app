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
}

package org.tumba.kegel_app.analytics

import javax.inject.Inject

class ReminderNotificationTracker @Inject constructor(private val analytics: Analytics) : TrackerScope {

    fun trackReminderNotificationShown() {
        analytics.track("reminder_notification_shown")
    }

    fun trackReminderNotificationClicked() {
        analytics.track(click("reminder_notification"))
    }
}

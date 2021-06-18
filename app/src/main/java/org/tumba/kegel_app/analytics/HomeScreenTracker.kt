package org.tumba.kegel_app.analytics

import javax.inject.Inject

class HomeScreenTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "HomeScreen")

    fun trackFirstTimeCustomizeClicked() {
        tracker.track(click("home_customize_first_time_exercise"))
    }

    fun trackCustomizeClicked() {
        tracker.track(click("home_customize_exercise"))
    }

    fun trackStartCustomExercise() {
        tracker.track(click("start_custom_exercise"))
    }
}

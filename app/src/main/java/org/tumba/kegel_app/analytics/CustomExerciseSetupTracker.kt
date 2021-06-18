package org.tumba.kegel_app.analytics

import javax.inject.Inject

class CustomExerciseSetupTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "CustomExerciseSetup")

    fun trackCustomExerciseSetupOpened() {
        tracker.track("custom_ex_setup_opened")
    }

    fun trackSetTenseDurationClicked() {
        tracker.track(click("custom_ex_set_tense_duration"))
    }

    fun trackSetRelaxDurationClicked() {
        tracker.track(click("custom_ex_set_relax_duration"))
    }

    fun trackSetRepeatsClicked() {
        tracker.track(click("custom_ex_set_repeats"))
    }

    fun trackStartClicked() {
        tracker.track(click("custom_ex_start"))
    }
}

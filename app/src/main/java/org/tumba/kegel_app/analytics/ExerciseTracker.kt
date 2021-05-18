package org.tumba.kegel_app.analytics

import androidx.core.os.bundleOf
import org.tumba.kegel_app.ui.exercise.ExerciseBackgroundMode
import javax.inject.Inject

class ExerciseTracker @Inject constructor(analytics: Analytics) : TrackerScope {

    private val tracker = ScreenTrackerHelper(analytics, "Exercise")

    fun trackPause() {
        tracker.track(click("pause"))
    }

    fun trackPlay() {
        tracker.track(click("play"))
    }

    fun trackStop() {
        tracker.track(click("stop"))
    }

    fun trackChangeVibration(enabled: Boolean) {
        tracker.track("change_vibration", bundleOf("enabled" to enabled))
    }

    fun trackChangeBackgroundMode(mode: ExerciseBackgroundMode) {
        tracker.track("change_background_mode", bundleOf("mode" to mode.ordinal))
        if (mode == ExerciseBackgroundMode.FLOATING_VIEW) {
            tracker.track("background_mode_floating_view_selected")
        }
    }

    fun trackChangeSound(enabled: Boolean) {
        tracker.track("change_sound", bundleOf("enabled" to enabled))
    }

    fun exitConfirmed() {
        tracker.track("exit_confirmed")
    }

    fun trackStarted(level: Int, day: Int) {
        tracker.track("started", bundleOf("level" to level, "day" to day))
    }

    fun trackFinished() {
        tracker.track("finished")
    }

    fun trackNavigateToProUpgradeFromBackgroundMode() {
        tracker.track("navigate_to_pro_upgrade_from_background_mode")
    }

    fun trackNavigateToProUpgradeFromMenuOption() {
        tracker.track("navigate_to_pro_upgrade_from_menu_option")
    }

    fun trackNavigateToProUpgradeFromSound() {
        tracker.track("navigate_to_pro_upgrade_from_sound")
    }
}

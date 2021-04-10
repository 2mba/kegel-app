package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.tumba.kegel_app.R
import org.tumba.kegel_app.analytics.SettingsTracker
import org.tumba.kegel_app.core.system.ResourceProvider
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.ui.common.SnackbarData
import org.tumba.kegel_app.utils.Event
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor,
    private val resourceProvider: ResourceProvider,
    private val tracker: SettingsTracker
) : BaseViewModel() {

    private val _days = MutableLiveData(settingsInteractor.getReminderDays())
    val days: LiveData<List<Boolean>> = _days

    val isReminderEnabled = settingsInteractor.observeReminderEnabled().asLiveData()

    private val _showReminderTimePickerDialog = MutableLiveData<Event<Boolean>>()
    val showReminderTimePickerDialog: LiveData<Event<Boolean>> = _showReminderTimePickerDialog

    private val _showLevelPickerDialog = MutableLiveData<Event<Boolean>>()
    val showLevelPickerDialog: LiveData<Event<Boolean>> = _showLevelPickerDialog

    private val _startReview = MutableLiveData<Event<Boolean>>()
    val startReview: LiveData<Event<Boolean>> = _startReview

    private val _showNightModeDialog = MutableLiveData<Event<Boolean>>()
    val showNightModeDialog: LiveData<Event<Boolean>> = _showNightModeDialog

    private val _nightMode = MutableLiveData(settingsInteractor.getNightMode())
    val nightMode: LiveData<Int> = _nightMode

    private val _reminderTime: Flow<SettingsInteractor.ReminderTime> = settingsInteractor.observeReminderTime()
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)
    val reminderTime = _reminderTime.asLiveData(Dispatchers.Default)
    val reminderTimeFormatted: LiveData<String> = reminderTime
        .map { time -> String.format("%02d:%02d", time.hour, time.minute) }

    val level = settingsInteractor.observeLevel().stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun onReminderDayClicked(idx: Int) {
        if (isReminderEnabled.value != true) return
        val days = days.value ?: return
        val newDays = List(DAYS_IN_WEEK) { listIdx ->
            if (listIdx == idx) !days[listIdx] else days[listIdx]
        }
        _days.value = newDays
        viewModelScope.launch {
            settingsInteractor.setReminderDays(newDays)
        }
        tracker.trackReminderDayChanged(idx, days[idx], days)
    }

    fun onReminderDayEnabledChanged(enabled: Boolean) {
        if (enabled == isReminderEnabled.value) return
        tracker.trackReminderEnabledChanged(enabled)
        viewModelScope.launch {
            settingsInteractor.setReminderEnabled(enabled)
        }
    }

    fun onReminderTimeClicked() {
        _showReminderTimePickerDialog.value = Event(true)
    }

    fun onSetLevelClicked() {
        tracker.trackSetCustomLevelClicked()
        _showLevelPickerDialog.value = Event(true)
    }

    fun onLevelSelected(level: Int) {
        viewModelScope.launch {
            tracker.trackCustomLevelSelected(level)
            settingsInteractor.setLevel(level)
            showSnackbar(
                SnackbarData(resourceProvider.getString(R.string.screen_settings_level_confirmation_snackbar))
            )
        }
    }

    fun onReminderTimeSelected(hour: Int, minute: Int) {
        tracker.trackReminderTimeChanged(hour, minute)
        viewModelScope.launch {
            settingsInteractor.setReminderTime(hour, minute)
            showSnackbar(
                SnackbarData(resourceProvider.getString(R.string.screen_settings_reminder_time_confirmation_snackbar))
            )
        }
    }

    fun onRateAppClicked() {
        tracker.trackRateAppClicked()
        _startReview.value = Event(true)
    }

    fun onNightModeClicked() {
        tracker.trackNightModeClicked()
        _showNightModeDialog.value = Event(true)
    }

    fun onNightModeSelected(mode: Int) {
        _nightMode.value = mode
        settingsInteractor.setNightMode(mode)
        viewModelScope.launch {
            delay(NIGHT_MODE_SNACKBAR_DELAY_MILLIS)
            showSnackbar(
                SnackbarData(resourceProvider.getString(R.string.screen_settings_night_mode_confirmation_snackbar))
            )
        }
        tracker.trackNightModeSelected()
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
        private const val NIGHT_MODE_SNACKBAR_DELAY_MILLIS = 500L
    }
}


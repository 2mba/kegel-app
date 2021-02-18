package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.tumba.kegel_app.analytics.SettingsTracker
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.utils.Event
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor,
    private val tracker: SettingsTracker
) : BaseViewModel() {

    private val _days = MutableLiveData(settingsInteractor.getReminderDays())
    val days: LiveData<List<Boolean>> = _days

    val isReminderEnabled = settingsInteractor.observeReminderEnabled().asLiveData()

    private val _showReminderTimePickerDialog = MutableLiveData<Event<Boolean>>()
    val showReminderTimePickerDialog: LiveData<Event<Boolean>> = _showReminderTimePickerDialog

    private val _showLevelPickerDialog = MutableLiveData<Event<Boolean>>()
    val showLevelPickerDialog: LiveData<Event<Boolean>> = _showLevelPickerDialog

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
        _showLevelPickerDialog.value = Event(true)
    }

    fun onLevelSelected(level: Int) {
        viewModelScope.launch {
            settingsInteractor.setLevel(level)
        }
    }

    fun onReminderTimeSelected(hour: Int, minute: Int) {
        tracker.trackReminderTimeChanged(hour, minute)
        viewModelScope.launch {
            settingsInteractor.setReminderTime(hour, minute)
        }
    }

    companion object {
        private const val DAYS_IN_WEEK = 7
    }
}


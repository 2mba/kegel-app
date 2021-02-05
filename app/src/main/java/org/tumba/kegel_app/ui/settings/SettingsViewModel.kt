package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import org.tumba.kegel_app.utils.Event
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor
) : BaseViewModel() {

    private val _days = MutableLiveData(settingsInteractor.getReminderDays())
    val days: LiveData<List<Boolean>> = _days

    val isReminderEnabled = settingsInteractor.observeReminderEnabled().asLiveData()

    private val _showReminderTimePickerDialog = MutableLiveData<Event<Boolean>>()
    val showReminderTimePickerDialog: LiveData<Event<Boolean>> = _showReminderTimePickerDialog

    private val _reminderTime: Flow<SettingsInteractor.ReminderTime> = settingsInteractor.observeReminderTime()
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)
    val reminderTime = _reminderTime.asLiveData(Dispatchers.Default)
    val reminderTimeFormatted: LiveData<String> = reminderTime
        .map { time -> String.format("%02d:%02d", time.hour, time.minute) }

    fun onReminderDayClicked(idx: Int) {
        val days = days.value ?: return
        val newDays = List(7) { listIdx ->
            if (listIdx == idx) !days[listIdx] else days[listIdx]
        }
        _days.value = newDays
        viewModelScope.launch {
            settingsInteractor.setReminderDays(newDays)
        }
    }

    fun onReminderDayEnabledChanged(enabled: Boolean) {
        if (enabled == isReminderEnabled.value) return
        viewModelScope.launch {
            settingsInteractor.setReminderEnabled(enabled)
        }
    }

    fun onReminderTimeClicked() {
        _showReminderTimePickerDialog.value = Event(true)
    }

    fun onResetLevelClicked() {
    }

    fun onSetLevelClicked() {
    }

    fun onReminderTimeSelected(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsInteractor.setReminderTime(hour, minute)
        }
    }
}


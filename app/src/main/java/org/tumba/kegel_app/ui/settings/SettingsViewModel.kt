package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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

    val reminderTime: LiveData<String> = settingsInteractor.observeReminderTime()
        .map { time -> String.format("%02d:%02d", time.hour, time.minute) }
        .asLiveData(Dispatchers.Default)

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


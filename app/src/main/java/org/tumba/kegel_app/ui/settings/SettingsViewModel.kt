package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.tumba.kegel_app.domain.interactor.SettingsInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsInteractor: SettingsInteractor
) : BaseViewModel() {

    private val _days = MutableLiveData(settingsInteractor.getReminderDays())
    val days: LiveData<List<Boolean>> = _days

    val isReminderEnabled = settingsInteractor.observeReminderEnabled().asLiveData()

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

    fun onResetLevelClicked() {
    }

    fun onSetLevelClicked() {
    }
}


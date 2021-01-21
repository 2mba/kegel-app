package org.tumba.kegel_app.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class SettingsViewModel @Inject constructor() : BaseViewModel() {

    private val _days =
        MutableLiveData(listOf(false, false, false, false, false, false, false))
    val days: LiveData<List<Boolean>> = _days


    fun onDayClicked(idx: Int) {
        val days = days.value ?: return
        _days.value = List(7) { listIdx ->
            if (listIdx == idx) !days[listIdx] else days[listIdx]
        }
    }

    fun onResetLevelClicked() {
    }

    fun onSetLevelClicked() {
    }
}


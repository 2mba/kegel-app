package org.tumba.kegel_app.ui.home.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.tumba.kegel_app.domain.interactor.ExerciseInteractor
import org.tumba.kegel_app.ui.common.BaseViewModel
import javax.inject.Inject

class FirstEntryViewModel @Inject constructor(
    private val exerciseInteractor: ExerciseInteractor
) : BaseViewModel() {

    val isAgreed = MutableLiveData(false)

    fun onClickSaveAgreement() {
        viewModelScope.launch {
            exerciseInteractor.setUserAgreementConfirmed()
        }
    }
}

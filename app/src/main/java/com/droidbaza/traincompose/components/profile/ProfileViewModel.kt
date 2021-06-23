package com.droidbaza.traincompose.components.profile

import androidx.lifecycle.ViewModel
import com.droidbaza.traincompose.logMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _newsState = MutableStateFlow(true)
    val newsState: StateFlow<Boolean> get() = _newsState


    init {
        logMsg("profie viewmodel init")
    }

    fun login(){
        _newsState.value = !newsState.value
    }
}
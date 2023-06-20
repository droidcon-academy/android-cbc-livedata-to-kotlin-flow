package com.droidcon.freshpassword

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(private val passwordRepository: PasswordRepository = PasswordRepositoryImpl()) :
    ViewModel() {

    fun fetchPassword() {
        viewModelScope.launch {
            _loading.value = true
            passwordRepository.refreshPassword()
            _loading.value = false
        }
    }

    val uiState = MediatorLiveData(UiState())

    init {
        uiState.addSource(passwordRepository.password) { password ->
            password?.let {
                uiState.value = uiState.value?.copy(password = it) ?: UiState(password = it)
            }

        }
        uiState.addSource(passwordRepository.history) { history ->
            history?.let {
                uiState.value = uiState.value?.copy(history = it) ?: UiState(history = it)

            }
        }
    }

    val error = passwordRepository.error

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading = _loading
}

data class UiState(val password: String = "", val history: List<String> = emptyList())

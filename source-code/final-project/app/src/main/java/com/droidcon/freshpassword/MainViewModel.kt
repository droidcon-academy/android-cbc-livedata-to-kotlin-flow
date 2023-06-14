package com.droidcon.freshpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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

    private val password = passwordRepository.password

    private val history = passwordRepository.history

    val uiState: StateFlow<UiState> = password.combine(history) { password, history ->
        UiState(password = password, history = history)
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5000),
        initialValue = UiState()
    )

    val error = passwordRepository.error

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
}

data class UiState(val password: String = "", val history: List<String> = emptyList())
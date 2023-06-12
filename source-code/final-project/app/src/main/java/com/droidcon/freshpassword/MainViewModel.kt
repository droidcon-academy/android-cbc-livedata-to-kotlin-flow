package com.droidcon.freshpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val password = passwordRepository.password

    val previousPasswords = passwordRepository.history

    val error = passwordRepository.error

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
}

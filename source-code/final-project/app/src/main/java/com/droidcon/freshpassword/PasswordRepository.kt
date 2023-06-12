package com.droidcon.freshpassword

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch

interface PasswordRepository {
    suspend fun refreshPassword()
    val password: StateFlow<String>
    val history: StateFlow<List<String>>
    val error: StateFlow<String?>
}

class PasswordRepositoryImpl(
    private val passwordApi: PasswordSource = PasswordSourceImpl(),
    // private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PasswordRepository {
    override suspend fun refreshPassword() {
        passwordApi.getPassword()
            //   .flowOn(dispatcher) // strictly not necessary since retrofit uses it's own dispatcher under the hood
            .catch { e -> _error.value = e.message }
            .collect {
                _history.value = updateHistory(_password.value, _history.value)
                // remember the fresh password
                _password.value = it
            }
    }

    internal fun updateHistory(password: String, existingHistory: List<String>): List<String> =
        // put the previous password in the list and only remember 3
        if (password.isNotEmpty()) {
            (listOf(password) + existingHistory).take(3)
        } else {
            existingHistory
        }

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    override val password: StateFlow<String> = _password.asStateFlow()

    private val _history: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    override val history: StateFlow<List<String>> = _history.asStateFlow()

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

}
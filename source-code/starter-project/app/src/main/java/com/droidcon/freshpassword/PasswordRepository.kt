package com.droidcon.freshpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface PasswordRepository {
    suspend fun refreshPassword()
    val password: LiveData<String>
    val history: LiveData<List<String>>
    val error: LiveData<String?>
}

class PasswordRepositoryImpl(
    private val passwordApi: PasswordSource = PasswordSourceImpl(),
    // private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PasswordRepository {
    override suspend fun refreshPassword() {
        try {
            val newPassword: String = passwordApi.getPassword()
            _history.value = updateHistory(_password.value, _history.value)
            // remember the fresh password
            _password.value = newPassword
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    internal fun updateHistory(password: String?, existingHistory: List<String>?): List<String>? {
        // put the previous password in the list and only remember 3
        password?.let {
            if (password.isNotEmpty()) {
                return (listOf(password) + (existingHistory ?: emptyList())).take(3)
            }
        }
        return existingHistory
    }

    private val _password: MutableLiveData<String> = MutableLiveData("")
    override val password: LiveData<String> = _password

    private val _history: MutableLiveData<List<String>> = MutableLiveData(emptyList())
    override val history: LiveData<List<String>> = _history

    private val _error: MutableLiveData<String?> = MutableLiveData(null)
    override val error: LiveData<String?> = _error

}
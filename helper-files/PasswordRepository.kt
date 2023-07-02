
val password: StateFlow<String>
val history: StateFlow<List<String>>
val error: StateFlow<String?>

private val _password: MutableStateFlow<String> = MutableStateFlow("")
override val password: StateFlow<String> = _password.asStateFlow()

private val _history: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
override val history: StateFlow<List<String>> = _history.asStateFlow()

private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
override val error: StateFlow<String?> = _error.asStateFlow()

passwordApi.getPassword()
    .catch { e -> _error.value = e.message }
    .collect {
        _history.value = updateHistory(_password.value, _history.value)
        // remember the fresh password
        _password.value = it
    }

internal fun updateHistory(password: String, existingHistory: List<String>): List<String> =
    // put the previous password in the list and only remember 3
    if (password.isNotEmpty()) {
        (listOf(password) + existingHistory).take(3)
    } else {
        existingHistory
    }
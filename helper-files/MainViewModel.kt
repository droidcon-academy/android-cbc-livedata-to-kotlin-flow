
private val password = passwordRepository.password
private val history = passwordRepository.history

val uiState: StateFlow<UiState> = password.combine(history) { password, history ->
    UiState(password = password, history = history)
}.stateIn(
    scope = viewModelScope,
    started = WhileSubscribed(5000),
    initialValue = UiState()
)

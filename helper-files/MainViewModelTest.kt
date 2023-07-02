
private val _password: MutableStateFlow<String> = MutableStateFlow("")
override val password: StateFlow<String> = _password.asStateFlow()
override val history: StateFlow<List<String>> = MutableStateFlow(passwordList).asStateFlow()
override val error: StateFlow<String?> = MutableStateFlow(errorMessage).asStateFlow()

@Test
fun `fetchPassword should fetch the first password`() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        viewModel.uiState.collect {}
    }
    assertEquals(viewModel.uiState.value.password, "")
    viewModel.fetchPassword()
    advanceUntilIdle()
    assertEquals("Password123", viewModel.uiState.value.password)
}

@Test
fun `fetchPassword twice should fetch the second password`() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        viewModel.uiState.collect {}
    }
    viewModel.fetchPassword()
    viewModel.fetchPassword()
    advanceUntilIdle()
    assertEquals("Password1234", viewModel.uiState.value.password)
}

@Test
fun `previousPasswords should be the same as history in repository`() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        viewModel.uiState.collect {}
    }
    assertEquals(passwordList, viewModel.uiState.value.history)
}

@Test
fun `error should contain errorMessage`() = runTest {
    assertEquals("Oops!", viewModel.error.value)
}
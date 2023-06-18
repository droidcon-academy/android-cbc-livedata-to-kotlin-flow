# Code snippets for each change

## PasswordSource.kt

```kotlin
interface PasswordSource {
    suspend fun getPassword(): Flow<String>
}
```

```kotlin
override suspend fun getPassword(): Flow<String> = flow {
    emit(api.getPassword().char.first())
}
```

## PasswordRepository.kt

```kotlin
val password: StateFlow<String>
val history: StateFlow<List<String>>
val error: StateFlow<String?>
```

```kotlin
private val _password: MutableStateFlow<String> = MutableStateFlow("")
override val password: StateFlow<String> = _password.asStateFlow()

private val _history: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
override val history: StateFlow<List<String>> = _history.asStateFlow()

private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
override val error: StateFlow<String?> = _error.asStateFlow()
```

```kotlin
passwordApi.getPassword()
    .catch { e -> _error.value = e.message }
    .collect {
        _history.value = updateHistory(_password.value, _history.value)
        // remember the fresh password
        _password.value = it
    }
```

```kotlin
internal fun updateHistory(password: String, existingHistory: List<String>): List<String> =
    // put the previous password in the list and only remember 3
    if (password.isNotEmpty()) {
        (listOf(password) + existingHistory).take(3)
    } else {
        existingHistory
    }
```

## PasswordRepositoryTest.kt

```kotlin
class FakePasswordSource(private val password: String) : PasswordSource {
    override suspend fun getPassword(): Flow<String> = flow {
        delay(1000)
        emit(password)
    }
}
```

```kotlin
@Test
fun `refreshPassword should update history`() = runTest {
        val history = repository.history
        repository.refreshPassword() // gets the first password
        repository.refreshPassword() // gets another password and puts the first one in the history
        assertContains(history.value, "password123")
    }
```

```kotlin
@Test
fun `refreshPassword should update history up to 3 values`() = runTest {
        val history = repository.history
        repository.refreshPassword() // gets the first password
        repository.refreshPassword() // gets another password and puts the first one in the history
        repository.refreshPassword() // gets another password and puts the first one in the history
        repository.refreshPassword() // gets another password and puts the first one in the history
        assertEquals(history.value.size, 3)
    }
```

## MainViewModel.kt

```kotlin
data class UiState(val password: String = "", val history: List<String> = emptyList())
```

```kotlin
private val password = passwordRepository.password
private val history = passwordRepository.history
```

```kotlin
val uiState: StateFlow<UiState> = password.combine(history) { password, history ->
    UiState(password = password, history = history)
}.stateIn(
    scope = viewModelScope,
    started = WhileSubscribed(5000),
    initialValue = UiState()
)
```

## MainViewModelTest.kt

```kotlin
private val _password: MutableStateFlow<String> = MutableStateFlow("")
override val password: StateFlow<String> = _password.asStateFlow()
override val history: StateFlow<List<String>> = MutableStateFlow(passwordList).asStateFlow()
override val error: StateFlow<String?> = MutableStateFlow(errorMessage).asStateFlow()
```

```kotlin
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
```

```kotlin
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
```

## PasswordFetch.kt

```kotlin
@Composable
fun PasswordFetch(
    modifier: Modifier = Modifier,
    uiState: UiState,
    loading: Boolean,
    shareText: (String) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = uiState.password,
                    style = MaterialTheme.typography.displayMedium
                )
                if (uiState.password.isNotEmpty()) {
                    Button(onClick = { shareText(uiState.password) }) {
                        Icon(
                            Icons.Rounded.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = onClick,
                enabled = !loading
            ) {
                Text("Fresh Password")
            }
            if (uiState.history.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = "History",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        items(uiState.history.size) { i ->
                            Text(modifier = Modifier.padding(8.dp), text = uiState.history[i])
                        }
                    }
                }
            }

        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultLoadingPreview() {
    FreshPasswordTheme {
        PasswordFetch(uiState = UiState(), loading = true)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPasswordPreview() {
    FreshPasswordTheme {
        PasswordFetch(uiState = UiState("Password123", history = emptyList()), loading = false)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPasswordWithHistoryPreview() {
    FreshPasswordTheme {
        PasswordFetch(
            uiState = UiState(
                "Unicorn",
                history = listOf("Password123", "Password1234", "Password12345")
            ),
            loading = false
        )
    }
}
```

## MainScreen.kt

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
val loading by viewModel.loading.collectAsStateWithLifecycle()
```

```kotlin
PasswordFetch(
    modifier = modifier,
    uiState = uiState,
    loading = loading,
    shareText = shareText,
    onClick = viewModel::fetchPassword
)
```

## app/build.gradle

Remove lines 86,87.88
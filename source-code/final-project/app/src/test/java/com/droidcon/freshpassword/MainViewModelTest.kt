package com.droidcon.freshpassword

import com.droidcon.freshpassword.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var passwordRepository: PasswordRepository
    private lateinit var viewModel: MainViewModel
    private val passwordList = listOf("Password123", "Password1234")

    @Before
    fun setup() {
        passwordRepository = FakePasswordRepository(
            passwordList = passwordList,
            errorMessage = "Oops!"
        )
        viewModel = MainViewModel(passwordRepository)
    }

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

}

class FakePasswordRepository(
    val passwordList: List<String>,
    errorMessage: String? = null
) : PasswordRepository {
    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    override val password: StateFlow<String> = _password.asStateFlow()
    override val history: StateFlow<List<String>> = MutableStateFlow(passwordList).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow(errorMessage).asStateFlow()

    private var refreshCount = 0
    override suspend fun refreshPassword() {
        delay(1000)
        if (passwordList.size > refreshCount) {
            _password.value = passwordList[refreshCount]
        }
        refreshCount++
        if (refreshCount == passwordList.size) refreshCount = 0
    }
}
package com.droidcon.freshpassword

import com.droidcon.freshpassword.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // TODO fix the tests to work with a UiState from the ViewModel

    @Test
    fun `fetchPassword should fetch the first password`() = runTest {
        assertEquals(viewModel.password.value, "")
        viewModel.fetchPassword()
        advanceUntilIdle()
        assertEquals(viewModel.password.value, "Password123")
    }

    @Test
    fun `fetchPassword twice should fetch the second password`() = runTest {
        viewModel.fetchPassword()
        viewModel.fetchPassword()
        advanceUntilIdle()
        assertEquals(viewModel.password.value, "Password1234")
    }

    @Test
    fun `previousPasswords should be the same as history in repository`() = runTest {
        assertEquals(viewModel.previousPasswords.value, passwordList)
    }

    @Test
    fun `error should contain errorMessage`() = runTest {
        assertEquals(viewModel.error.value, "Oops!")
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
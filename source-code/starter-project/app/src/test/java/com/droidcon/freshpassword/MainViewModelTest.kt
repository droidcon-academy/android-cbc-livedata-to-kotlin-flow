package com.droidcon.freshpassword

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.droidcon.freshpassword.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

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
    fun `fetchPassword should set loading and then clear loading`() = runTest {
        assertEquals(false, viewModel.loading.value)
        viewModel.fetchPassword()
        assertEquals(true, viewModel.loading.value )
        advanceUntilIdle()
        assertEquals(false, viewModel.loading.value )
    }

    @Test
    fun `fetchPassword should fetch the first password`() = runTest {
        assertEquals("", viewModel.password.value)
        viewModel.fetchPassword()
        advanceUntilIdle()
        assertEquals("Password123", viewModel.password.value )
    }

    @Test
    fun `fetchPassword twice should fetch the second password`() = runTest {
        viewModel.fetchPassword()
        viewModel.fetchPassword()
        advanceUntilIdle()
        assertEquals("Password1234", viewModel.password.value)
    }

    @Test
    fun `previousPasswords should be the same as history in repository`() = runTest {
        assertEquals(passwordList, viewModel.history.value)
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
    private val _password: MutableLiveData<String> = MutableLiveData("")
    override val password: LiveData<String> = _password
    override val history: LiveData<List<String>> = MutableLiveData(passwordList)
    override val error: LiveData<String?> = MutableLiveData(errorMessage)

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
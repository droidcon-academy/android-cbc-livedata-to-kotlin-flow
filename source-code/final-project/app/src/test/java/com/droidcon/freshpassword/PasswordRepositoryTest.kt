package com.droidcon.freshpassword

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordRepositoryTest {

    private  lateinit var fakePasswordSource:PasswordSource
    private  lateinit var repository:PasswordRepositoryImpl

    @Before
    fun setup() {
        fakePasswordSource = FakePasswordSource("password123")
        repository = PasswordRepositoryImpl(fakePasswordSource)
    }

    @Test
    fun `refreshPassword should update password`() = runTest {
        val actualFlow = repository.password
        repository.refreshPassword()
        assertEquals("password123", actualFlow.value)
    }

    @Test
    fun `refreshPassword should not update the history on the first call`() = runTest {
        val history = repository.history
        repository.refreshPassword() // gets the first password
        assertContentEquals(history.value, emptyList())
    }

    @Test
    fun `refreshPassword should update history`() = runTest {
        val history = repository.history
        repository.refreshPassword() // gets the first password
        repository.refreshPassword() // gets another password and puts the first one in the history
        assertContains(history.value, "password123")
    }

    @Test
    fun `refreshPassword should update history up to 3 values`() = runTest {
        val history = repository.history
        repository.refreshPassword() // gets the first password
        repository.refreshPassword() // gets another password and puts the first one in the history
        repository.refreshPassword() // gets another password and puts the first one in the history
        repository.refreshPassword() // gets another password and puts the first one in the history
        assertEquals(history.value.size, 3)
    }

    @Test
    fun `updateHistory should add a password to an empty list`() {
        val history = repository.updateHistory("Unicorn", emptyList())
        assertEquals(listOf("Unicorn"), history)
    }

    @Test
    fun `updateHistory should add a password to a list of 2`() {
        val history = repository.updateHistory("Unicorn", listOf("Password123", "Password1234"))
        assertEquals(listOf("Unicorn", "Password123", "Password1234"), history)
    }

    @Test
    fun `updateHistory should limit the list to three`() {
        val history = repository.updateHistory("Zombie", listOf("Unicorn", "Password123", "Password1234"))
        assertEquals(listOf("Zombie", "Unicorn", "Password123"), history)

    }
}

class FakePasswordSource(private val password: String) : PasswordSource {
    //    override suspend fun getPassword(): Flow<String> = flowOf(password)
    override suspend fun getPassword(): Flow<String> = flow {
        delay(1000)
        emit(password)
    }
}
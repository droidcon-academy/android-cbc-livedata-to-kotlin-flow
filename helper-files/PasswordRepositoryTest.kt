
class FakePasswordSource(private val password: String) : PasswordSource {
    override suspend fun getPassword(): Flow<String> = flow {
        delay(1000)
        emit(password)
    }
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

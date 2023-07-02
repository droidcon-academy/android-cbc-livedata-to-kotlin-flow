interface PasswordSource {
    suspend fun getPassword(): Flow<String>
}

override suspend fun getPassword(): Flow<String> = flow {
    emit(api.getPassword().char.first())
}
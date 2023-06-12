package com.droidcon.freshpassword

import com.droidcon.freshpassword.api.PasswordServiceFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PasswordSource {
    suspend fun getPassword(): Flow<String>
}

class PasswordSourceImpl : PasswordSource {
    private val api = PasswordServiceFactory.instance
    override suspend fun getPassword(): Flow<String> = flow {
        emit(api.getPassword().char.first())
    }
}
package com.droidcon.freshpassword

import com.droidcon.freshpassword.api.PasswordServiceFactory

interface PasswordSource {
    suspend fun getPassword(): String
}

class PasswordSourceImpl : PasswordSource {
    private val api = PasswordServiceFactory.instance
    override suspend fun getPassword(): String =
        api.getPassword().char.first()

}
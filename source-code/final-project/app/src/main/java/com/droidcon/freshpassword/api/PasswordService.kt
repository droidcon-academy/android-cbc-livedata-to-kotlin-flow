package com.droidcon.freshpassword.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PasswordApi {
    @GET("query")
    suspend fun getPassword(
        @Query("command") command: String = "password",
        @Query("format") format: String = "json",
        @Query("count") count: Int = 1,
        //  @Query("scheme") scheme: String = "RN#nrV",
    ): PasswordResponse
}

object PasswordServiceFactory {
    private lateinit var retrofit: Retrofit
    private var baseUrl = "https://www.passwordrandom.com/"
    val instance: PasswordApi by lazy {
        buildPasswordApi(baseUrl.toHttpUrl())
    }

    /**
     * static method to initialise the http client and retrofit class
     */
    private fun buildPasswordApi(url: HttpUrl): PasswordApi {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.NONE
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(PasswordApi::class.java)
    }
}

data class PasswordResponse(val char: List<String>)

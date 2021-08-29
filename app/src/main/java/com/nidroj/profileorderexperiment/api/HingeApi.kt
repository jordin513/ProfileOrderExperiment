package com.nidroj.profileorderexperiment.api

import com.nidroj.profileorderexperiment.data.model.ConfigResponse
import com.nidroj.profileorderexperiment.data.model.UsersResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HingeApi {

    companion object {
        private const val BASE_URL = "http://hinge-ue1-dev-cli-android-homework.s3-website-us-east-1.amazonaws.com/"

        @Volatile
        private var INSTANCE: HingeApi? = null
        fun createApi(): HingeApi {

            INSTANCE?.let {
                return it
            } ?: synchronized(this) {

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(HingeApi::class.java)

                return INSTANCE!!

            }
        }
    }

    @GET("users")
    suspend fun getUsers(): Response<UsersResponse>

    @GET("config")
    suspend fun getConfig(): Response<ConfigResponse>
}
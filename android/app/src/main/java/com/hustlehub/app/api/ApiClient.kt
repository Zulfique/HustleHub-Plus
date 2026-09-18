package com.hustlehub.app.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hustlehub.app.BuildConfig
import com.hustlehub.app.model.ErrorResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = BuildConfig.HUSTLEHUB_BASE_URL

    private val gson: Gson = GsonBuilder().create()

    private val okHttpClient: OkHttpClient by lazy {
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level(HttpLoggingInterceptor.Level.BASIC)
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()
        } else {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
        }
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
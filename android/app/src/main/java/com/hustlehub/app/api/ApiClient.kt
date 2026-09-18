package com.hustlehub.app.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hustlehub.app.HustleHubApplication
import com.hustlehub.app.R
import com.hustlehub.app.model.ErrorResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.HttpException
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object ApiClient {

    private const val BASE_URL = BuildConfig.HUSTLEHUB_BASE_URL

    private val gson: Gson = GsonBuilder().create()

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level(HttpLoggingInterceptor.Level.BASIC)

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    fun parseErrorMessage(e: Throwable): String {
        return when (e) {
            is HttpException -> {
                try {
                    val body = e.response()?.errorBody()?.string()
                    val error = gson.fromJson(body, ErrorResponse::class.java)
                    error.message ?: "Request failed (${e.code()})"
                } catch (ex: Exception) {
                    "Request failed (${e.code()})"
                }
            }
            is java.net.ConnectException -> {
                "Cannot reach server. Make sure the backend is running."
            }
            is java.net.UnknownHostException -> {
                "Cannot reach server. Check network connection."
            }
            is javax.net.ssl.SSLException -> {
                "SSL connection failed. Certificate mismatch."
            }
            is java.net.SocketTimeoutException -> {
                "Request timed out. Server may be unreachable."
            }
            else -> e.message ?: "An unexpected error occurred"
        }
    }
}

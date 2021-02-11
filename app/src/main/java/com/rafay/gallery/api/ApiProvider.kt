package com.rafay.gallery.api

import com.rafay.gallery.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Interface for class which provides concrete implementation of api endpoints
 */
interface ApiProvider {

    val pixabayApi: PixabayApi
}

/**
 * Concrete implementation of [ApiProvider] using [Retrofit] and [OkHttpClient]
 */
class RetrofitApiProvider : ApiProvider {

    private val retrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
            .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .callTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor {
                val httpUrl =
                    it.request().url.newBuilder()
                        .addQueryParameter(QUERY_PARAM_KEY, BuildConfig.API_KEY)
                        .build()
                val request = it.request().newBuilder().url(httpUrl).build()
                it.proceed(request)
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    )
                }
            }
            .build()

        retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(Images.Hit.Type.JsonAdapter()).build()
                )
            )
            .build()
    }

    override val pixabayApi: PixabayApi = retrofit.create(PixabayApi::class.java)

    companion object {
        private const val QUERY_PARAM_KEY = "key"
        private const val REQUEST_TIMEOUT_SECONDS = 5L
    }
}

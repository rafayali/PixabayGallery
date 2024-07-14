package com.rafay.gallery.di

import com.rafay.gallery.api.ApiProvider
import com.rafay.gallery.api.RetrofitApiProvider
import org.koin.dsl.module

val appModule =
    module {
        single<ApiProvider> { RetrofitApiProvider() }
    }

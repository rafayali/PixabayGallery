package com.rafay.gallery.flow.home

import com.rafay.gallery.api.ApiProvider
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel(get<ApiProvider>().pixabayApi) }
}

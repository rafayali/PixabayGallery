package com.rafay.gallery.flow.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafay.gallery.api.PixabayApi
import com.rafay.gallery.common.Event
import com.rafay.gallery.common.State
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val pixabayApi: PixabayApi) : ViewModel() {

    private var job: Job? = null

    private var page: Int = 1

    private var shouldLoad = true

    private val _state = MutableLiveData<State<List<HomeItem>>>(State.Loading)
    val state: LiveData<State<List<HomeItem>>> = _state

    private val _error = MutableLiveData<Event<Error>>()
    val error: LiveData<Event<Error>> = _error

    init {
        load()
    }

    /**
     * Returns currently loaded images
     */
    fun getImages(): List<HomeItem> {
        if (_state.value is State.Success) {
            return (_state.value as State.Success<List<HomeItem>>).data
        } else {
            throw IllegalStateException("Cannot get data in ${_state.value} state")
        }
    }

    fun retry() {
        job?.cancel()
        shouldLoad = true
        page = 1
        _state.postValue(State.Loading)
        load()
    }

    fun loadMore() {
        if (!shouldLoad) return
        page++
        load()
    }

    /**
     * Loads images data from Pixabay api
     */
    private fun load() {
        if (!shouldLoad) return

        job = viewModelScope.launch {
            shouldLoad = false

            val result = runCatching { pixabayApi.getImages(page) }

            result.exceptionOrNull()?.let { throwable ->
                Timber.e(throwable)
                if (throwable !is CancellationException) {
                    if (_state.value !is State.Success) {
                        _state.postValue(State.Retry)
                    } else {
                        _error.postValue(Event(Error.Generic))
                    }
                }
                return@launch
            }

            with(result.getOrThrow()) {
                hits.map {
                    it.toHomeImageItem()
                }.also {
                    val images = mutableListOf<HomeItem>()

                    with(_state.value as? State.Success<List<HomeItem>>) {
                        if (this != null) images.addAll(this.data)
                    }

                    images.addAll(it)

                    if (images.count() < totalHits) shouldLoad = true

                    _state.postValue(State.Success<List<HomeItem>>(images))
                }
            }
        }
    }

    enum class Error {
        Generic
    }
}

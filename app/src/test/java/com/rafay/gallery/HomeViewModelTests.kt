package com.rafay.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rafay.gallery.api.PixabayApi
import com.rafay.gallery.common.Event
import com.rafay.gallery.common.State
import com.rafay.gallery.common.TestLiveDataObserver
import com.rafay.gallery.data.fakeImageApiResponse
import com.rafay.gallery.screens.home.HomeItem
import com.rafay.gallery.screens.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTests {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val testStateObserver = TestLiveDataObserver<State<List<HomeItem>>>()

    private val testErrorObserver = TestLiveDataObserver<Event<HomeViewModel.Error>>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `state should emit loading and then success after successful response`() = runTest {
        val mockPixabayApi = mockk<PixabayApi>()
        coEvery { mockPixabayApi.getImages(any()) } returns fakeImageApiResponse

        val viewModel = HomeViewModel(pixabayApi = mockPixabayApi)
        viewModel.state.observeForever(testStateObserver)

        assert(testStateObserver.values.firstOrNull() is State.Loading)

        assert(testStateObserver.values.drop(1).firstOrNull() is State.Success)

        viewModel.state.removeObserver(testStateObserver)
    }

    @Test
    fun `state should emit loading and then retry when there is an exception on first api response`() =
        runTest {
            val mockPixabayApi = mockk<PixabayApi>()
            coEvery { mockPixabayApi.getImages(any()) } throws Exception()

            val viewModel = HomeViewModel(mockPixabayApi)
            viewModel.state.observeForever(testStateObserver)

            assert(testStateObserver.values.firstOrNull() is State.Loading)

            assert(testStateObserver.values.drop(1).firstOrNull() is State.Retry)

            viewModel.state.removeObserver(testStateObserver)
        }

    @Test
    fun `should call pixabayApi getImages exactly once after creating viewModel`() = runTest {
        val mockPixabayApi =
            mockk<PixabayApi>(relaxed = true) {
                coEvery { getImages(any()) } returns fakeImageApiResponse
            }

        val viewModel = HomeViewModel(mockPixabayApi)

        coVerify(exactly = 1) { mockPixabayApi.getImages(any()) }

        viewModel.state.removeObserver(testStateObserver)
    }

    @Test
    fun `_state should emit success on first and any follow up results when server return 200`() =
        runTest {
            val mockPixabayApi =
                mockk<PixabayApi>(relaxed = true) {
                    coEvery {
                        getImages(any())
                    } coAnswers {
                        fakeImageApiResponse
                    } coAndThen {
                        fakeImageApiResponse
                    }
                }

            val viewModel =
                HomeViewModel(mockPixabayApi).apply {
                    state.observeForever(testStateObserver)
                    error.observeForever(testErrorObserver)
                    loadMore()
                }

            assert(testStateObserver.values.firstOrNull() is State.Loading)
            assert(testStateObserver.values.drop(1).firstOrNull() is State.Success)
            assert(testStateObserver.values.drop(2).firstOrNull() is State.Success)
            assert(testErrorObserver.values.isEmpty())

            coVerify(exactly = 2) { mockPixabayApi.getImages(any()) }

            viewModel.state.removeObserver(testStateObserver)
            viewModel.error.removeObserver(testErrorObserver)
        }

    @After
    fun tearDown() {
        testStateObserver.clear()
        testErrorObserver.clear()
        Dispatchers.resetMain()
    }
}

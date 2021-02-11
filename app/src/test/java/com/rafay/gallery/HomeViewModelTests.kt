package com.rafay.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rafay.gallery.api.Images
import com.rafay.gallery.api.PixabayApi
import com.rafay.gallery.common.CoroutineTestRule
import com.rafay.gallery.common.Event
import com.rafay.gallery.common.State
import com.rafay.gallery.common.TestLiveDataObserver
import com.rafay.gallery.flow.home.HomeItem
import com.rafay.gallery.flow.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTests {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private val testStateObserver = TestLiveDataObserver<State<List<HomeItem>>>()

    private val testErrorObserver = TestLiveDataObserver<Event<HomeViewModel.Error>>()

    @After
    fun tearDown() {
        testStateObserver.clear()
        testErrorObserver.clear()
    }

    @Test
    fun `state should emit loading and then success after successful response`() =
        coroutinesTestRule.runBlockingTest {
            coroutinesTestRule.testCoroutineDispatcher.pauseDispatcher()

            val mockPixabayApi = mockk<PixabayApi>()
            coEvery { mockPixabayApi.getImages(any()) } returns fakeGetImagesApiResult()

            val viewModel = HomeViewModel(pixabayApi = mockPixabayApi)
            viewModel.state.observeForever(testStateObserver)

            assert(testStateObserver.values.firstOrNull() is State.Loading)

            coroutinesTestRule.testCoroutineDispatcher.resumeDispatcher()

            assert(testStateObserver.values.drop(1).firstOrNull() is State.Success)

            viewModel.state.removeObserver(testStateObserver)
        }

    @Test
    fun `state should emit loading and then retry when there is an exception on first api response`() =
        coroutinesTestRule.testCoroutineDispatcher.runBlockingTest {
            coroutinesTestRule.testCoroutineDispatcher.pauseDispatcher()

            val mockPixabayApi = mockk<PixabayApi>()
            coEvery { mockPixabayApi.getImages(any()) } throws Exception()

            val viewModel = HomeViewModel(mockPixabayApi)
            viewModel.state.observeForever(testStateObserver)

            assert(testStateObserver.values.firstOrNull() is State.Loading)

            coroutinesTestRule.testCoroutineDispatcher.resumeDispatcher()

            assert(testStateObserver.values.drop(1).firstOrNull() is State.Retry)

            viewModel.state.removeObserver(testStateObserver)
        }

    @Test
    fun `should call pixabayApi getImages exactly once after creating viewModel`() =
        coroutinesTestRule.runBlockingTest {
            coroutinesTestRule.testCoroutineDispatcher.pauseDispatcher()

            val mockPixabayApi = mockk<PixabayApi>(relaxed = true) {
                coEvery { getImages(any()) } returns fakeGetImagesApiResult()
            }

            val viewModel = HomeViewModel(mockPixabayApi)

            coroutinesTestRule.testCoroutineDispatcher.resumeDispatcher()

            coVerify(exactly = 1) { mockPixabayApi.getImages(any()) }

            viewModel.state.removeObserver(testStateObserver)
        }

    @Test
    fun `_state should emit success on first and any follow up results when server return 200`() =
        coroutinesTestRule.runBlockingTest {
            coroutinesTestRule.testCoroutineDispatcher.pauseDispatcher()

            val mockPixabayApi = mockk<PixabayApi>(relaxed = true) {
                coEvery {
                    getImages(any())
                } coAnswers {
                    fakeGetImagesApiResult()
                } coAndThen {
                    fakeGetImagesApiResult()
                }
            }

            val viewModel = HomeViewModel(mockPixabayApi).apply {
                state.observeForever(testStateObserver)
                error.observeForever(testErrorObserver)
                loadMore()
            }

            coroutinesTestRule.testCoroutineDispatcher.resumeDispatcher()

            assert(testStateObserver.values.firstOrNull() is State.Loading)
            assert(testStateObserver.values.drop(1).firstOrNull() is State.Success)
            assert(testStateObserver.values.drop(2).firstOrNull() is State.Success)
            assert(testErrorObserver.values.isEmpty())

            coVerify(exactly = 2) { mockPixabayApi.getImages(any()) }

            viewModel.state.removeObserver(testStateObserver)
            viewModel.error.removeObserver(testErrorObserver)
        }

    private fun fakeGetImagesApiResult() = Images(
        1,
        1,
        listOf(
            Images.Hit(
                1L,
                "",
                Images.Hit.Type.Photo,
                "",
                "",
                1,
                1,
                "",
                1,
                1,
                "",
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                "",
                ""
            )
        )
    )
}
package com.sample.knowquake.ui.earthquakes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sample.knowquake.api.ApiService
import com.sample.knowquake.rx.TestSchedulerProvider
import com.sample.knowquake.vo.EarthQuake
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Test
import com.sample.knowquake.result.EventObserver
import com.sample.knowquake.util.LiveDataTestUtil
import io.reactivex.Observable
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn


@RunWith(MockitoJUnitRunner::class)
class EarthQuakeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Execute on main thread instantly

    @Mock
    lateinit var apiService: ApiService

    private lateinit var earthQuakeViewModel: EarthQuakeViewModel

    private lateinit var scheduler: TestScheduler

    @Mock
    lateinit var response: EarthQuake

    @Before
    fun setUp() { // This method is call before each test run.
        scheduler = TestScheduler()
        earthQuakeViewModel = EarthQuakeViewModel(apiService, TestSchedulerProvider(scheduler))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun earthquake_feature_test() {
        doReturn(Observable.just(response))
            .`when`(apiService)
            .earthQuakeFeatures(limit = 1, offset = 1)
        earthQuakeViewModel.isProgressShown.observeForever(
            EventObserver {
                if (it) {
                    assertTrue("Loading Success", true)
                } else {
                    assertFalse("Loading Finished", it)
                }
            })
        earthQuakeViewModel.earthQuakeFeatures(1, 1, false)
        scheduler.triggerActions()
        assertEquals(response, LiveDataTestUtil.getValue(earthQuakeViewModel.earthQuake))
    }

}
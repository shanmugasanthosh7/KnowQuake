package com.sample.knowquake.ui.earthquakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.knowquake.api.ApiService
import com.sample.knowquake.result.Event
import com.sample.knowquake.rx.SchedulerProvider
import com.sample.knowquake.vo.EarthQuake
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class EarthQuakeViewModel
@Inject constructor(private val apiService: ApiService, private val scheduler: SchedulerProvider) : ViewModel() {

    private val disposable = CompositeDisposable()

    private val _isProgressShown = MutableLiveData<Event<Boolean>>()

    private val _onClick = MutableLiveData<Event<Navigate>>()

    private val _earthQuake = MutableLiveData<EarthQuake>()

    private val _earthQuakeError = MutableLiveData<Throwable>()

    val earthQuake: LiveData<EarthQuake> get() = _earthQuake

    val earthQuakeError: LiveData<Throwable> get() = _earthQuakeError

    val onClick: LiveData<Event<Navigate>> get() = _onClick

    val isProgressShown: LiveData<Event<Boolean>> get() = _isProgressShown

    fun earthQuakeFeatures(limit: Int, offset: Int, isLoadMoreEnabled: Boolean) {
        disposable.add(apiService.earthQuakeFeatures(limit = limit, offset = offset)
            .subscribeOn(scheduler.executorIo())
            .observeOn(scheduler.ui())
            .subscribe(
                { _earthQuake.value = it },
                { _earthQuakeError.value = it },
                { if (!isLoadMoreEnabled) _isProgressShown.value = Event(false) },
                { if (!isLoadMoreEnabled) _isProgressShown.value = Event(true) }
            )
        )
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

}

enum class Navigate {
    ON_CLICK_ITEM
}
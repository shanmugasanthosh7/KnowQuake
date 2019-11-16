package com.sample.knowquake.ui.earthquakedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.knowquake.util.TimeUtils
import com.sample.knowquake.vo.EqFeatures
import javax.inject.Inject
import kotlin.math.roundToInt

class EarthquakeDetailsViewModel @Inject constructor() : ViewModel() {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title
    private val _timeDepth = MutableLiveData<String>()
    val timeDepth: LiveData<String>
        get() = _timeDepth
    private val _reviewStatus = MutableLiveData<String>()
    val reviewStatus: LiveData<String>
        get() = _reviewStatus
    private val _magnitude = MutableLiveData<String>()
    val magnitude: LiveData<String>
        get() = _magnitude
    private val _type = MutableLiveData<String>()
    val type: LiveData<String>
        get() = _type

    fun setEarthquakeDetails(features: EqFeatures) {
        _title.value = features.properties.title.toUpperCase()
        _timeDepth.value = String.format(
            "%s | %s km depth",
            TimeUtils.getUnixTimestampToDate(
                features.properties.time,
                TimeUtils.DATE_TIME_FORMAT_2,
                true
            ), features.geometry.coordinates[features.geometry.coordinates.size - 1].toDouble().roundToInt()
        )
        _reviewStatus.value = features.properties.status
        _magnitude.value =
            String.format("%s %s", features.properties.mag.toDouble().roundToInt(), features.properties.magType)
        _type.value = features.properties.type

    }

}
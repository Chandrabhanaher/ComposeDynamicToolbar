package com.chandra.composedynamictoolbar

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arcgismaps.geometry.Point
import com.arcgismaps.location.Location
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application):AndroidViewModel(application){

    private val _mapState = MutableStateFlow(ArcGISMap(BasemapStyle.ArcGISTopographicBase))
    val mapState: StateFlow<ArcGISMap?> = _mapState

    fun changeBasemap(style: BasemapStyle) {
        _mapState.value = ArcGISMap(style)
    }

    private val _catalyst = MutableLiveData(false)
    val catalyst :LiveData<Boolean> = _catalyst

    private val _distometer = MutableLiveData(false)
    val distometer :LiveData<Boolean> = _distometer

    fun catalystSwitch(){
        _catalyst.value = !_catalyst.value!!
    }

    fun distometerSwitch(){
        _distometer.value = !_distometer.value!!
    }

//    Current Location

    private val _getLocation = MutableLiveData<Int>(0)
    val getLocation: LiveData<Int> = _getLocation

    fun getMyLocation(i: Int) {
        _getLocation.value = i
    }
}
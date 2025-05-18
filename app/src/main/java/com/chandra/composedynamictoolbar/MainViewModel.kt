package com.chandra.composedynamictoolbar

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application):AndroidViewModel(application){

    private val _mapState = MutableStateFlow(ArcGISMap(BasemapStyle.ArcGISTopographicBase))
    val mapState: StateFlow<ArcGISMap?> = _mapState


    private val _selectedBasemapStyle = MutableStateFlow<BasemapStyle>(BasemapStyle.ArcGISTopographic)
    val selectedBasemapStyle: StateFlow<BasemapStyle> = _selectedBasemapStyle

    fun updateBasemapStyle(style: BasemapStyle) {
        _selectedBasemapStyle.value = style
    }

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

    private val _kml = MutableStateFlow<Uri?>(null)
    val kmlPath get() = _kml.asStateFlow()

    fun kmlPath(path: Uri?) {
        _kml.value = path!!
    }
}
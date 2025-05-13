package com.chandra.composedynamictoolbar

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.location.NmeaLocationDataSource
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay

@Composable
fun ArcGisMapView(paddingValues: PaddingValues, context: Context ,viewModel: MainViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val currentBaseMap = viewModel.mapState.collectAsState().value ?: ArcGISMap(BasemapStyle.ArcGISTopographic)

    val nmeaLocationDataSource = NmeaLocationDataSource(SpatialReference.wgs84())
    val locationDisplay = rememberLocationDisplay().apply {
        setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
    }

    var currentPosition by remember { mutableStateOf<Point?>(null) }


    LaunchedEffect(Unit) {
        locationDisplay.dataSource.start()
    }
    val setLocation by viewModel.getLocation.observeAsState()
    if(setLocation != 0){
        LaunchedEffect(setLocation) {
            locationDisplay.setAutoPanMode(LocationDisplayAutoPanMode.Navigation)
            locationDisplay.dataSource.start()
            viewModel.getMyLocation(0)
        }
    }

//    if(checkPermissions(context = context)){
//        LaunchedEffect(Unit) {
//            locationDisplay.dataSource.start()
////            locationDisplay.dataSource.locationChanged.collect { location ->
////                currentPosition = location.position
////            }
//        }
//    }else{
//        RequestPermissions(context = context, onPermissionsGranted = {
//            coroutineScope.launch {
//                locationDisplay.dataSource.start()
//            }
//        })
//    }

    currentBaseMap.apply {
        initialViewpoint = Viewpoint(
            latitude = 34.0270,
            longitude = -118.8050,
            scale = 7200.0
        )
    }



    MapView(modifier = Modifier
        .fillMaxSize().padding(paddingValues),
        arcGISMap = currentBaseMap,
        locationDisplay = locationDisplay,
        onMapRotationChanged = {},
        onSingleTapConfirmed = {tapPoint->
            currentPosition = tapPoint.mapPoint!!
        },
        content = {

           /* currentPosition?.let {
                Callout(location = currentPosition!!,
                    modifier = Modifier.wrapContentSize()) {
                    Column {
                        Text(text = "Tapped Point: ${currentPosition!!.x},${currentPosition!!.y}")
                    }
                }
            }*/
        }
    )
}
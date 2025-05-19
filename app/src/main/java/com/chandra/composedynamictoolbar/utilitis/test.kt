package com.chandra.composedynamictoolbar.utilitis

MapView(modifier = Modifier
.fillMaxSize()
.padding(paddingValues),
arcGISMap = currentBaseMap,
locationDisplay = locationDisplay);



import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Layer
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView

class MapViewModel : ViewModel() {
    // LiveData to represent layers and overlays in the Map
    val layers = MutableLiveData<List<Layer>>()
    val overlays = MutableLiveData<List<GraphicsOverlay>>()

    // Remove a layer from the ArcGISMap
    fun removeLayer(mapView: MapView, layerName: String) {
        val map = mapView.map as ArcGISMap
        val layerToRemove = map.operationalLayers.firstOrNull { it.name == layerName }
        layerToRemove?.let {
            map.operationalLayers.remove(it)
            layers.postValue(map.operationalLayers)
        }
    }

    // Remove an overlay from the MapView
    fun removeOverlay(mapView: MapView, overlayName: String) {
        val overlayToRemove = mapView.graphicsOverlays.firstOrNull { it.name == overlayName }
        overlayToRemove?.let {
            mapView.graphicsOverlays.remove(it)
            overlays.postValue(mapView.graphicsOverlays)
        }
    }
}
//=========================

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.view.MapView

class MapActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen(mapViewModel)
        }
    }
}

@Composable
fun MapScreen(viewModel: MapViewModel) {
    // Remember MapView with lifecycle handling
    val mapView = rememberMapViewWithLifecycle()

    // LiveData or State for layers and overlays
    val layers = viewModel.layers.collectAsState(emptyList())
    val overlays = viewModel.overlays.collectAsState(emptyList())

    // Define your current Base Map and Location Display (passed as parameters)
    val currentBaseMap = ArcGISMap() // Example, use your actual base map
    val locationDisplay = null // Replace with your actual LocationDisplay if needed

    // MapView that is configured with your map and location display
    MapView(
        modifier = Modifier
            .fillMaxSize(),
        arcGISMap = currentBaseMap,
        locationDisplay = locationDisplay
    )

    // Buttons to remove layers and overlays
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            viewModel.removeLayer(mapView, "LayerName")
        }) {
            Text("Remove Layer")
        }

        Button(onClick = {
            viewModel.removeOverlay(mapView, "OverlayName")
        }) {
            Text("Remove Overlay")
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // Ensure MapView is disposed of correctly
    DisposableEffect(mapView) {
        onDispose {
            mapView.dispose()
        }
    }
    return mapView
}
//============================
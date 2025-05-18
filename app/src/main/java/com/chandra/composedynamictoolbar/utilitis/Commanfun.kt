package com.chandra.composedynamictoolbar.utilitis

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.layers.KmlLayer
import com.arcgismaps.mapping.view.MapView
import com.chandra.composedynamictoolbar.MainViewModel
import com.chandra.composedynamictoolbar.R
import java.io.File
import java.io.FileOutputStream


@Composable
fun ChangeBaseMap(paddingValues: PaddingValues, viewModel: MainViewModel) {
    val isCatalyst by viewModel.catalyst.observeAsState(false)
    val isDistometer by viewModel.distometer.observeAsState(false)
    Column(modifier = Modifier.wrapContentSize().padding(paddingValues),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center) {

        val basemapOptions = listOf(
            "Topographic" to BasemapStyle.ArcGISTopographicBase,
            "Imagery" to BasemapStyle.ArcGISImageryStandard,
            "Streets" to BasemapStyle.ArcGISStreets,
            "Oceans" to BasemapStyle.ArcGISOceans
        )
        var expanded by remember { mutableStateOf(false) }
        // Dropdown to change basemap

        Box(modifier = Modifier.padding(top=10.dp, end = 8.dp)) {
            IconButton(onClick = {
                expanded = !expanded
            }){
                Icon(painter = painterResource(R.drawable.ic_layers), contentDescription = "Select Layer",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(25.dp))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
                modifier = Modifier.background(MaterialTheme.colorScheme.onBackground)
            ) {
                basemapOptions.forEach { (label, style) ->
                    DropdownMenuItem(
                        text = { Text(text = label, color = MaterialTheme.colorScheme.background) },
                        onClick = {
                            viewModel.changeBasemap(style)
                            expanded = !expanded
                        }
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Catalyst", style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            ))
            Spacer(modifier = Modifier.width(2.dp))
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = isCatalyst,
                onCheckedChange = { viewModel.catalystSwitch() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Blue,
                    checkedTrackColor = Color.Blue.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Red,
                    uncheckedTrackColor = Color.Red.copy(alpha = 0.5f),
                ),
                thumbContent = {
                    if (isCatalyst){
                        Text("ON", style = TextStyle(fontSize = 10.sp))
                    }else{
                        Text("OFF",style = TextStyle(fontSize = 10.sp))
                    }
                }
            )
        }

        Row(modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Distometer", style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground
            ))
            Spacer(modifier = Modifier.width(2.dp))
            Switch(
                modifier = Modifier.scale(0.7f),
                checked = isDistometer,
                onCheckedChange = { viewModel.distometerSwitch()},
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Blue,
                    checkedTrackColor = Color.Blue.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Red,
                    uncheckedTrackColor = Color.Red.copy(alpha = 0.5f),
                ),
                thumbContent = {
                    if (isDistometer){
                        Text("ON", style = TextStyle(fontSize = 10.sp))
                    }else{
                        Text("OFF",style = TextStyle(fontSize = 10.sp))
                    }
                }
            )
        }
        IconButton(onClick = {
            viewModel.getMyLocation(1)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_my_location),
                contentDescription = "My Location",
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp).padding(end = 8.dp)
            )
        }

    }
}



@Composable
fun RequestPermissions(context: Context, onPermissionsGranted: ()-> Unit) {
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            onPermissionsGranted()
        } else {
            showError(context, "Location permissions were denied")
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }
}

fun checkPermissions(context: Context): Boolean {
    val permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val permissionCheckFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return permissionCheckCoarseLocation && permissionCheckFineLocation
}

fun showError(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun removeLayerFromMap(context: Context,mapView: MapView, kmlLayers: MutableList<KmlLayer>, kmlPath: Uri?) {
    val path  = copyKmlUriToTempFile(context, kmlPath!!)
    val layers = mapView.map!!.operationalLayers
    val layerToRemove = kmlLayers.find { it.dataset.uri == path }

    layerToRemove?.let {
        layers.remove(it)
        kmlLayers.remove(it)
        Log.d("KML", "Layer removed from map: $path")
    }
}
/*fun removeLayerFromMap(path: String, kmlLayers: MutableList<KmlLayer>) {

    val layers = mapView.map.operationalLayers
    val layerToRemove = kmlLayers.find { it.dataset?.path == path }

    layerToRemove?.let {
        layers.remove(it)
        kmlLayers.remove(it)
        Log.d("KML", "Layer removed from map: $path")
    }
}*/

fun copyKmlUriToTempFile(context: Context, kmlUri: Uri): String {
    val tempFile = File(context.cacheDir, "temp.kml")
    context.contentResolver.openInputStream(kmlUri)?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }
    return tempFile.absolutePath
}

/*
fun loadAndZoomKml(path: String, mapView: MapView?, kmlLayers: MutableList<KmlLayer>) {
    if (mapView == null) return
    val kmlDataset = KmlDataset(path, PathType.Absolute)
    val kmlLayer = KmlLayer(kmlDataset)
    mapView.map.operationalLayers.clear() // Clear previous layers
    mapView.map.operationalLayers.add(kmlLayer)
    kmlLayers.add(kmlLayer) // Add the new KML layer to the list

    CoroutineScope(Dispatchers.Main).launch {
        val result = kmlLayer.load()
        result.onSuccess {
            kmlLayer.fullExtent?.let { extent ->
                mapView.setViewpointGeometry(extent, 50.0)
            }
        }.onFailure {
            Log.e("KML", "Failed to load: ${it.message}")
        }
    }
}*/

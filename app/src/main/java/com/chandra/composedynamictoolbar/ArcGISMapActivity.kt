package com.chandra.composedynamictoolbar

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.Color
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.GeometryType
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.kml.KmlDataset
import com.arcgismaps.mapping.layers.KmlLayer
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.mapping.view.geometryeditor.GeometryEditor
import com.chandra.composedynamictoolbar.ui.theme.ComposeDynamicToolbarTheme
import com.chandra.composedynamictoolbar.utilitis.RequestPermissions
import com.chandra.composedynamictoolbar.utilitis.copyKmlUriToTempFile
import com.chandra.composedynamictoolbar.utilitis.removeLayerFromMap
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ArcGISMapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ArcGISEnvironment.apiKey = ApiKey.create("AAPT85fOqywZsicJupSmVSCGrnTWuY3dxr_XClY4tlFDwwxRM74D716knpzP8IUrh63zAdb_HT6MUm9lzMNBB8y2XLf6Rf0zq5RE9yFw9z_WheLtqUrMmCLBFA172915PwNPfvLiUJwxWtyIfdOzU39nfOfegG_gzCuJtdPdGwSx7t-h2Uy9obspqT4MwHsrux62Hr2IkTrrxTNigMPgH8YBUSpG9jAe8GP3S3PdA2r4lqU.AT2_2826mlK2")
        ArcGISEnvironment.applicationContext = applicationContext
        setContent {
            ComposeDynamicToolbarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ViewMap(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ViewMap(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    RequestPermissions(context){}


    val intent = remember {
        Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
            data = Uri.parse("package:" + context.packageName)
        }
    }

    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true // No need on Android < 11
    }
    val permissionGranted by remember { mutableStateOf(hasPermission) }

    if(!permissionGranted){
        intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    val mapView = remember {
        MapView(context).apply {
            map = ArcGISMap(BasemapStyle.ArcGISImageryStandard)
        }
    }
    lifecycleOwner.lifecycle.addObserver(mapView)
    val viewModel = MainViewModel(application = Application())
    ArcGISMapView(mapView,modifier,viewModel,context)

}

@Composable
fun ArcGISMapView(mapView: MapView, modifier: Modifier, viewModel: MainViewModel, context: Context) {
    val selectedBasemapStyle by viewModel.selectedBasemapStyle.collectAsState()


    var baseMap by remember { mutableStateOf(ArcGISMap(selectedBasemapStyle))}

    var kmlUri by remember { mutableStateOf<Uri?>(null) }
    val graphicsOverlay = remember { GraphicsOverlay() }
    val graphicsOverlays = remember { listOf(graphicsOverlay) }
    val geometryEditor = remember { GeometryEditor() }


    LaunchedEffect(Unit) {
        mapView.locationDisplay.apply {
            setAutoPanMode(LocationDisplayAutoPanMode.Recenter)
            this.dataSource.start()
        }
        mapView.graphicsOverlays.addAll(graphicsOverlays)
        mapView.geometryEditor = geometryEditor



    }

    val kmlPath by viewModel.kmlPath.collectAsState()
    val kmlLayers = remember { mutableListOf<KmlLayer>() }

    LaunchedEffect(kmlPath) {
        if (kmlPath != null){

            val tempFile = copyKmlUriToTempFile(context, kmlUri!!)

            val kmlDataset = KmlDataset(tempFile)
            val kmlLayer = KmlLayer(kmlDataset)
            //mapView.map!!.operationalLayers.clear()
            mapView.map!!.operationalLayers.add(kmlLayer)

            launch {
                kmlLayer.load()
            }


            launch {
                kmlLayer.loadStatus.collect {
                    when(it){
                        LoadStatus.Loaded->{
                            val extent = kmlLayer.fullExtent
                            kmlLayers.add(kmlLayer)
                            if (extent != null){
                                val name = kmlDataset.rootNodes.firstOrNull()?.name
                                Log.e("MY_MAP", "Kml layer name $name")
                                mapView.setViewpointGeometry(extent, 50.0)
                            }
                        }
                        LoadStatus.NotLoaded->{
                            Log.e("MY_MAP", "Kml  is not loaded")
                        }
                        else->{

                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedBasemapStyle) {
        baseMap = ArcGISMap(selectedBasemapStyle)
        mapView.map = baseMap
    }


    val pickKmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                kmlUri = uri
                viewModel.kmlPath(uri)
            }
        }
    )

    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )
    BasemapDropdown(
        selectedStyle = selectedBasemapStyle,
        onStyleSelected = viewModel::updateBasemapStyle
    )

    Column(modifier = modifier
        .wrapContentSize(align = Alignment.BottomEnd)
        .padding(45.dp),
        verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = {
                pickKmlLauncher.launch(arrayOf("application/vnd.google-earth.kml+xml", "application/xml", "text/xml"))

//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "application/vnd.google-earth.kml+xml"
//                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                pickKmlLauncher.launch(intent)
            }) {
            Text("Select KML File")
        }

        Button(onClick = {
            removeLayerFromMap(context,mapView, kmlLayers, kmlPath)
        }) {
            Text("Remove layer")
        }

        Button(onClick = {
            geometryEditor.start(GeometryType.Point)
            val graphic = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle, Color.red, 10f)
            graphicsOverlay.graphics.add(Graphic(geometryEditor.geometry.value, graphic))
        }) {
            Text("Draw point")
        }
    }
}

@Composable
fun BasemapDropdown(selectedStyle: BasemapStyle, onStyleSelected: (BasemapStyle)-> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val basemapOptions = mapOf(
        "Topographic" to BasemapStyle.ArcGISTopographic,
        "Streets" to BasemapStyle.ArcGISStreets,
        "Imagery" to BasemapStyle.ArcGISImagery,
        "Navigation" to BasemapStyle.ArcGISNavigation,
        "Dark Gray" to BasemapStyle.ArcGISDarkGray,
        "Light Gray" to BasemapStyle.ArcGISLightGray
    )
    val selectedName = basemapOptions.entries.firstOrNull {
        it.value == selectedStyle
    }?.key ?: "Topographic"

    Box(modifier = Modifier
        .padding(45.dp)
        .wrapContentSize()){
        OutlinedButton(onClick = {expanded = true}) {
            Text("Basemap $selectedName")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = {expanded = !expanded}) {
            basemapOptions.forEach { (name, style) ->
                DropdownMenuItem(
                    text = { Text(name)},
                    onClick = {
                        onStyleSelected(style)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArcGISMapPreview() {
    ComposeDynamicToolbarTheme {
        ViewMap()
    }
}
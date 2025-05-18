package com.chandra.composedynamictoolbar

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment

import com.chandra.composedynamictoolbar.model.DrawerItem
import com.chandra.composedynamictoolbar.ui.theme.ComposeDynamicToolbarTheme
import com.chandra.composedynamictoolbar.utilitis.ChangeBaseMap
import com.chandra.composedynamictoolbar.utilitis.RequestPermissions
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ArcGISEnvironment.apiKey = ApiKey.create("AAPT85fOqywZsicJupSmVSCGrnTWuY3dxr_XClY4tlFDwwxRM74D716knpzP8IUrh63zAdb_HT6MUm9lzMNBB8y2XLf6Rf0zq5RE9yFw9z_WheLtqUrMmCLBFA172915PwNPfvLiUJwxWtyIfdOzU39nfOfegG_gzCuJtdPdGwSx7t-h2Uy9obspqT4MwHsrux62Hr2IkTrrxTNigMPgH8YBUSpG9jAe8GP3S3PdA2r4lqU.AT2_2826mlK2")
        ArcGISEnvironment.applicationContext = applicationContext
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen(){
    ComposeDynamicToolbarTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            ShowNavigationContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowNavigationContent(){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Toolbar state
    var toolbarTitle by remember { mutableStateOf("Home") }
    var toolbarIcon by remember { mutableStateOf(Icons.Default.Home) }

    val drawerItems = listOf(
        DrawerItem("Home", Icons.Default.Home),
        DrawerItem("Profile", Icons.Default.Person),
    )

   val settingsItem =  DrawerItem("Settings", Icons.Default.Settings)

    var expanded by remember { mutableStateOf(false) }

    val menuItems = listOf("Account", "Preferences", "Logout")
    var selectedItem by remember { mutableStateOf<String?>(null) }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("App Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                drawerItems.forEach { item->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.title ) },
                        label = { Text(item.title)},
                        selected = toolbarTitle == item.title,
                        onClick = {
                            toolbarTitle = item.title
                            toolbarIcon = item.icon
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                NavigationDrawerItem(
                    icon = { Icon(settingsItem.icon, contentDescription = settingsItem.title ) },
                    label = { Text(settingsItem.title)},
                    selected = toolbarTitle == settingsItem.title,
                    onClick = {
                        toolbarTitle = settingsItem.title
                        toolbarIcon = settingsItem.icon
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ){
        Scaffold(modifier = Modifier.fillMaxWidth(),
            topBar = {
                TopAppBar(
                    title = { Text(toolbarTitle) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {

                        IconButton(onClick = {
                            expanded  = true
                        }) {
                            Icon(
                                imageVector = toolbarIcon,
                                contentDescription = "Current Section",
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }



                        if(toolbarTitle == "Settings"){
                            DropdownMenu(expanded = expanded, onDismissRequest = {expanded  = false}) {

                                menuItems.forEach { dItem->
                                    DropdownMenuItem(
                                        text = { Text(dItem)},
                                        onClick = {
                                            expanded = false
                                            selectedItem = dItem
                                        }
                                    )
                                }
                            }
                        }else{

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }) {padding ->

            Greeting(padding)
        }
    }
}

@Composable
fun Greeting(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val viewModel = MainViewModel(application = Application())
    var expanded by remember { mutableStateOf(false) }
    RequestPermissions(context){}
    ArcGisMapView(paddingValues, context,viewModel)
    ChangeBaseMap(paddingValues, viewModel)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeScreen()
}
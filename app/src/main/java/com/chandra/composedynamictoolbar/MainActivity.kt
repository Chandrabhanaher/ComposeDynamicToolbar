package com.chandra.composedynamictoolbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chandra.composedynamictoolbar.model.DrawerItem
import com.chandra.composedynamictoolbar.ui.theme.ComposeDynamicToolbarTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                    }
                )
            }) {padding ->
            Greeting(name = "Chandrabhan", modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HomeScreen()
}
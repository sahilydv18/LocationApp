package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    val context = LocalContext.current  // Context of the current screen we are a
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, context = context)
}


@Composable
fun LocationDisplay(locationUtils: LocationUtils, context: Context) {

    // This requestPermissionLauncher gives us UI for asking permission to the user and we can either request single permission or multiple permissions
    val requestPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
        permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // When we ask for permission and the user gives us the permission then we write the code that will be executed in that case here
        } else {
            // If user declines our permission we then give them reason for taking permission to run app using rationale
            // rationale helps us to ask again for permission if we are denied permission for the first time but it is only limited to one time
            // means we can only ask again for permission only once after getting rejected for first time
            // Rationale basically helps us to ask for permission a second time if we are denied for the first time
            val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if(rationaleRequired) {
                // If user declines permission for first time show the user this toast and now we can ask for permission one more time
                Toast.makeText(context,"Location Permission required to run app", Toast.LENGTH_LONG).show()
            } else {
                // If user declines permission for the second time then show this toast and now we can't ask for permission again, user needs to go to settings to turn on permission
                Toast.makeText(context,"Turn on location from setting", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Location not available")
        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                // We have location
            } else {
                // Here we start the launcher for the first time, as we start the app for first time we don't have any permissions so we are asking for permission
                // via requestPermissionLauncher for location(fine and coarse)
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
}
package com.example.droneapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.droneapp.ui.theme.DroneAppTheme
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DroneAppTheme {
                NavLogic()
            }
        }
    }
}

@Composable
fun NavLogic() {
    val droneViewModel: DroneViewModel = viewModel()
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "home") {
        composable("home") { ButtonScreen(navController) }
        composable("map") {
            MapScreen(drones = droneViewModel.drones.value, droneViewModel = droneViewModel)
        }
        composable("set_destination") {
            SetDestinationScreen(drones = droneViewModel.drones.value) { drone, destination ->
                droneViewModel.setDestination(drone, destination)
                navController.popBackStack()
            }
        }
        composable("text1") { SimpleTextScreen("Acesta este ecranul 1") }
        composable("drone_info") { DroneInfoScreen(drones = droneViewModel.drones.value) }
    }
}


@Composable
fun ButtonScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("map") }) {
            Text("Vezi Harta")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("text1") }) {
            Text("Buton 2")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("drone_info") }) {
            Text("Vezi Drone")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("set_destination") }) {
            Text("Setează destinația dronei")
        }
    }
}


@Composable
fun MapScreen(drones: List<Drone>, droneViewModel: DroneViewModel) {
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(44.4268, 26.1025), 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        drones.forEach { drone ->
            Marker(
                state = MarkerState(position = drone.coordinates),
                title = drone.name,
                snippet = "Viteză: ${drone.speed} km/h"
            )
        }
    }

    LaunchedEffect(Unit) {
        droneViewModel.fetchDrones()
        while (true) {
            delay(3000)
            droneViewModel.updateAllDrones()
            println("3s Debug check")
        }
    }
}


@Composable
fun SetDestinationScreen(drones: List<Drone>, onSetDestination: (Drone, LatLng) -> Unit) {
    val selectedDrone = remember { mutableStateOf<Drone?>(null) }
    val destination = remember { mutableStateOf(LatLng(45.4268, 26.1025)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Selectează o dronă și setează destinația")
        drones.forEach { drone ->
            Button(
                onClick = { selectedDrone.value = drone },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Selectează ${drone.name}")

            }
        }

        selectedDrone.value?.let { drone ->
            Text("Drona selectată: ${drone.name} (${drone.model})")


            Button(
                onClick = {
                    onSetDestination(drone, destination.value)
                    println("onClick check: ${drone.name}, ${destination.value}")
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Setează destinația")
            }
        }
    }
}

@Composable
fun SimpleTextScreen(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = text)
    }
}

@Composable
fun DroneInfoScreen(drones: List<Drone>) {
    Column(modifier = Modifier.padding(16.dp)) {
        drones.forEach { drone ->
            Text("Nume: ${drone.name}")
            Text("Model: ${drone.model}")
            Text("Locație: ${drone.location}")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonScreenPreview() {
    DroneAppTheme {
        ButtonScreen(navController = rememberNavController())
    }
}
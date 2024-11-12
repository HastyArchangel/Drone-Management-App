package com.example.droneapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import java.util.Locale


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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedButton(
                isVisible = true,
                onClick = { navController.navigate("map") },
                text = "Vezi Harta",
                alpha = 0.7f
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedButton(
                isVisible = true,
                onClick = { navController.navigate("text1") },
                text = "Buton 2",
                alpha = 0.7f
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedButton(
                isVisible = true,
                onClick = { navController.navigate("drone_info") },
                text = "Vezi Drone",
                alpha = 0.7f
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedButton(
                isVisible = true,
                onClick = { navController.navigate("set_destination") },
                text = "Setează destinația dronei",
                alpha = 0.7f
            )
        }
    }
}

@Composable
fun AnimatedButton(isVisible: Boolean, onClick: () -> Unit, text: String, alpha: Float = 0.7f) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) + scaleIn(initialScale = 0.5f, animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(durationMillis = 500)) + scaleOut(targetScale = 0.5f, animationSpec = tween(500))
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0x00FFFF).copy(alpha = alpha)
            )
        ) {
            Text(text = text)
        }
    }
}



@Composable
fun MapScreen(drones: List<Drone>, droneViewModel: DroneViewModel) {
    var scale by remember { mutableStateOf(0.5f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        scale = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale, alpha = animatedScale)
            .animateContentSize()
    ) {
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
    }

    LaunchedEffect(Unit) {
        droneViewModel.fetchDrones()
        while (true) {
            delay(3000)
            droneViewModel.fetchDrones()
        }
    }
}



@Composable
fun SetDestinationScreen(drones: List<Drone>, onSetDestination: (Drone, LatLng) -> Unit) {
    val selectedDrone = remember { mutableStateOf<Drone?>(null) }
    var destination by remember { mutableStateOf(LatLng(42.4268, 26.1025)) }

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

            Spacer(modifier = Modifier.height(16.dp))

            val cameraPositionState = rememberCameraPositionState {
                position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(destination, 10f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize().weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    destination = latLng
                }
            ) {
                Marker(
                    state = MarkerState(position = destination),
                    title = "Destinația selectată"
                )
            }

            Button(
                onClick = {
                    onSetDestination(drone, destination)
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        drones.forEach { drone ->
            Text(
                text = "Nume: ${drone.name}",
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF9D),
                fontSize = 18.sp
            )
            Text(
                text = "Model: ${drone.model}",
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF9D),
                fontSize = 16.sp
            )
            Text(
                text = "Locație: ${drone.location}",
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF9D),
                fontSize = 16.sp
            )
            Text(
                text = "Coordonate: ${String.format(Locale.US, "%.3f", drone.coordinates.latitude)}, " +
                        String.format(Locale.US, "%.3f", drone.coordinates.longitude),
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FF9D),
                fontSize = 16.sp
            )
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
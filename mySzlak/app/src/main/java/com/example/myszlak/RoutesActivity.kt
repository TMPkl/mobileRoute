package com.example.myszlak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme

// Model danych trasy
data class Route(
    val name: String,
    val length: String,
    val difficulty: String
)

class RoutesActivity : ComponentActivity() {

    // Hardcoded dane
    private val bikingRoutes = listOf(
        Route("Szlak Orlich Gniazd", "164 km", "Średni"),
        Route("Trasa Wigierska", "85 km", "Łatwy"),
        Route("Szlak Piastowski", "230 km", "Trudny"),
        Route("Trasa Bieszczadzka", "120 km", "Trudny"),
        Route("Szlak Doliny Baryczy", "92 km", "Łatwy"),
        Route("Szlak Doliny Baryczy", "92 km", "Łatwy"),
        Route("Szlak Doliny Baryczy", "92 km", "Łatwy"),
        Route("Szlak Doliny Baryczy", "92 km", "Łatwy"),

    )

    private val hikingRoutes = listOf(
        Route("Rysy", "22 km", "Trudny"),
        Route("Morskie Oko", "10 km", "Łatwy"),
        Route("Dolina Chochołowska", "18 km", "Średni"),
        Route("Giewont", "14 km", "Średni"),
        Route("Babia Góra", "16 km", "Trudny"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MySzlakTheme {
                val viewModel: BaseViewModel = viewModel()

                // Pobieramy początkowy typ z intencji tylko przy pierwszym uruchomieniu
                LaunchedEffect(Unit) {
                    if (viewModel.activityType.isEmpty()) {
                        viewModel.activityType = intent.getStringExtra("activityType") ?: "piesze"
                    }
                }

                // Wybieramy listę na podstawie stanu w ViewModelu
                val routes = if (viewModel.activityType == "rowerowe") bikingRoutes else hikingRoutes
                val title = if (viewModel.activityType == "rowerowe") "Trasy rowerowe" else "Trasy piesze"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Przycisk powrotu po prawej u góry
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Przyciski do zmiany typu tras
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { viewModel.activityType = "rowerowe" },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                colors = if (viewModel.activityType == "rowerowe") 
                                    ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Text(text = "rowerowe", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.activityType = "piesze" },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                                colors = if (viewModel.activityType == "piesze") 
                                    ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Text(text = "piesze", fontWeight = FontWeight.Bold)
                            }
                        }

                        // Lista tras
                        RoutesList(
                            title = title,
                            routes = routes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoutesList(title: String, routes: List<Route>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Nagłówek
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Lista tras
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routes) { route ->
                RouteCard(route = route)
            }
        }
    }
}

@Composable
fun RouteCard(route: Route) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = route.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dystans: ${route.length}", fontSize = 14.sp)
            Text(text = "Trudność: ${route.difficulty}", fontSize = 14.sp)
        }
    }
}

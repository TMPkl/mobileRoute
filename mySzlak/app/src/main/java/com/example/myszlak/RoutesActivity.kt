package com.example.myszlak

import android.content.Intent
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme

class RoutesActivity : ComponentActivity() {
    private fun navigateToRouteDetails(trailID: Int) {
        val intent = Intent(this, TrailDetailsActivity::class.java).apply {
            putExtra("selectedTrail", trailID)
        }
        startActivity(intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MySzlakTheme {
                val baseViewModel: BaseViewModel = viewModel()
                val trailViewModel: TrailViewModel = viewModel()

                val trails by trailViewModel.trails.collectAsState()
                val isLoading by trailViewModel.isLoading.collectAsState()
                val errorMessage by trailViewModel.errorMessage.collectAsState()

                // Pobieramy typ z intencji przy pierwszym uruchomieniu
                LaunchedEffect(Unit) {
                    if (baseViewModel.activityType.isEmpty()) {
                        baseViewModel.activityType = intent.getStringExtra("activityType") ?: "rowerowe"
                    }
                    // Ładujemy dane z API od razu
                    if (baseViewModel.activityType == "rowerowe") {
                        trailViewModel.loadCyclingTrails()
                    } else {
                        trailViewModel.loadWalkingTrails()
                    }
                }

                val title = if (baseViewModel.activityType == "rowerowe") "Trasy rowerowe" else "Trasy piesze"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
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

                        val options = listOf("rowerowe", "biegowe")

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                    onClick = {
                                        baseViewModel.activityType = label
                                        // Przy zmianie zakładki ładujemy odpowiednie trasy
                                        if (label == "rowerowe") {
                                            trailViewModel.loadCyclingTrails()
                                        } else {
                                            trailViewModel.loadWalkingTrails()
                                        }
                                    },
                                    selected = baseViewModel.activityType == label
                                ) {
                                    Text(text = label, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Spinner podczas ładowania
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        errorMessage?.let {
                            Text(
                                text = "Błąd: $it",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        if (!isLoading && errorMessage == null) {
                            RoutesList(
                                title = title,
                                routes = trails,
                                modifier = Modifier.weight(1f),
                                onClick = { trailID -> navigateToRouteDetails(trailID)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RoutesList(title: String, routes: List<Trail>, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routes) { route ->
                RouteCard(route = route, onClick = { onClick(route.id)})
            }
        }
    }
}

@Composable
fun RouteCard(route: Trail, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick =  onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = route.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dystans: ${route.length} km", fontSize = 14.sp)
            val shortDesc = if (route.description.length > 60) {
                route.description.substring(0, 60) + "..."
            } else {
                route.description
            }
            Text(text = shortDesc, fontSize = 14.sp)
        }
    }
}

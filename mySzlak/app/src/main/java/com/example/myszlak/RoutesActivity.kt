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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme
import com.example.myszlak.Trail
// Model danych trasy


class RoutesActivity : ComponentActivity() {

    // Hardcoded dane
    private val routes= listOf(
        Trail(10,"abcde","opisssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",10.0f, TrailType.WALKING,"zdjecie"),
        Trail(10,"abcde","opis",10.0f, TrailType.WALKING,"zdjecie"),
        Trail(10,"abcde","opis",10.0f, TrailType.WALKING,"zdjecie"),
        Trail(10,"abcde","opis",10.0f, TrailType.CYCLING,"zdjecie"),
        Trail(10,"abcde","opis",10.0f, TrailType.CYCLING,"zdjecie"),
        Trail(10,"abcde","opis",10.0f, TrailType.CYCLING,"zdjecie"))


    @OptIn(ExperimentalMaterial3Api::class)
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
                //val routes = if (viewModel.activityType == "rowerowe") bikingRoutes else hikingRoutes
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
                        val options = listOf("rowerowe", "biegowe")
                        var selectedIndex by remember { mutableStateOf(0) }

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            options.forEachIndexed { index, label ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                                    onClick = {
                                        selectedIndex = index
                                        viewModel.activityType = label
                                    },
                                    selected = viewModel.activityType == label
                                ) {
                                    Text(text = label, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Lista tras
                        val filteredRoutes = when (viewModel.activityType) {
                            "rowerowe" -> routes.filter { it.type == TrailType.CYCLING }
                            "biegowe" -> routes.filter { it.type == TrailType.WALKING }
                            else -> emptyList()
                        }
                        RoutesList(
                            title = title,
                            routes = filteredRoutes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoutesList(title: String, routes: List<Trail>, modifier: Modifier = Modifier) {
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
fun RouteCard(route: Trail) {
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
            val shortDesc = if (route.description.length > 20) {
                route.description.substring(0, 20) + "..."
            } else {
                route.description
            }
            Text(text = "Opis: ${shortDesc}", fontSize = 14.sp)
        }
    }
}


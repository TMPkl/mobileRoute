package com.example.myszlak

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                val stopwatchViewModel: StopwatchViewModel = viewModel()

                val trails by trailViewModel.trails.collectAsState()
                val isLoading by trailViewModel.isLoading.collectAsState()
                val errorMessage by trailViewModel.errorMessage.collectAsState()
                val selectedTrail by trailViewModel.selectedTrail.collectAsState()

                var searchQuery by remember { mutableStateOf("") }
                val filteredTrails = remember(trails, searchQuery) {
                    trails.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }

                val configuration = LocalConfiguration.current
                val isTablet = configuration.screenWidthDp >= 600
                val context = LocalContext.current

                // Pobieramy typ z intencji przy pierwszym uruchomieniu
                LaunchedEffect(Unit) {
                    stopwatchViewModel.bindService(context)
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

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        StopwatchFab(stopwatchViewModel)
                    }
                ) { innerPadding ->
                    if (isTablet) {
                        // Widok dla tabletu: Lista | Szczegóły
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Lewa strona - Lista
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(end = 8.dp)
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

                                FilterSegmentedButtons(baseViewModel, trailViewModel)
                                SearchField(query = searchQuery, onQueryChange = { searchQuery = it })

                                if (isLoading && trails.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    RoutesList(
                                        title = title,
                                        routes = filteredTrails,
                                        trailViewModel = trailViewModel,
                                        modifier = Modifier.weight(1f),
                                        onClick = { trailID ->
                                            if (trailID > 2000) {
                                                trailViewModel.loadCyclingTrail(trailID)
                                            } else {
                                                trailViewModel.loadWalkingTrail(trailID)
                                            }
                                        }
                                    )
                                }
                            }

                            // Prawa strona - Szczegóły
                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .fillMaxHeight()
                                    .padding(start = 8.dp)
                            ) {
                                if (selectedTrail != null) {
                                    TrailDetailsView(
                                        trail = selectedTrail!!,
                                        stopwatchViewModel = stopwatchViewModel,
                                        trailViewModel = trailViewModel
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Wybierz trasę z listy, aby zobaczyć szczegóły",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Widok dla telefonu
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

                            FilterSegmentedButtons(baseViewModel, trailViewModel)
                            SearchField(query = searchQuery, onQueryChange = { searchQuery = it })

                            // Spinner podczas ładowania
                            if (isLoading && trails.isEmpty()) {
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

                            if (!isLoading || trails.isNotEmpty()) {
                                RoutesList(
                                    title = title,
                                    routes = filteredTrails,
                                    trailViewModel = trailViewModel,
                                    modifier = Modifier.weight(1f),
                                    onClick = { trailID -> navigateToRouteDetails(trailID) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Szukaj trasy...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSegmentedButtons(baseViewModel: BaseViewModel, trailViewModel: TrailViewModel) {
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
}

@Composable
fun RoutesList(
    title: String,
    routes: List<Trail>,
    trailViewModel: TrailViewModel,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
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
                RouteCard(
                    route = route,
                    trailViewModel = trailViewModel,
                    onClick = { onClick(route.id) }
                )
            }
        }
    }
}

@Composable
fun RouteCard(route: Trail, trailViewModel: TrailViewModel, onClick: () -> Unit) {
    val isFavorite by trailViewModel.isFavorite(route.id).collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Dystans: ${route.length} km", fontSize = 14.sp)
            }
            
            IconButton(
                onClick = { trailViewModel.toggleFavorite(route) }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Ulubione",
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

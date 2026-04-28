package com.example.myszlak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun TrailDetailsView(
    trail: Trail,
    stopwatchViewModel: StopwatchViewModel,
    trailViewModel: TrailViewModel,
    modifier: Modifier = Modifier
) {
    var imageLoading by remember { mutableStateOf(true) }
    val currentStopwatchTrailId by stopwatchViewModel.currentTrailId.collectAsState()
    val isStopwatchRunning by stopwatchViewModel.isRunning.collectAsState()
    val elapsedSeconds by stopwatchViewModel.elapsedSeconds.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            AsyncImage(
                model = "https://mapi.kaleszynski.xyz/img/${trail.imageId}",
                contentDescription = trail.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                onSuccess = { imageLoading = false },
                onError = { imageLoading = false }
            )
            if (imageLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = trail.name,
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Dystans: ${trail.length} km",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(15.dp))
        
        Text(
            text = "Opis: ${trail.description}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(30.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Stoper",
                    style = MaterialTheme.typography.titleLarge
                )
                
                val isThisTrailActive = currentStopwatchTrailId == trail.id
                
                Text(
                    text = if (isThisTrailActive) formatTime(elapsedSeconds) else "00:00",
                    style = MaterialTheme.typography.displayMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isThisTrailActive) {
                        Button(
                            onClick = { 
                                if (isStopwatchRunning) stopwatchViewModel.pause() 
                                else stopwatchViewModel.start(trail.id, trail.name) 
                            }
                        ) {
                            Text(if (isStopwatchRunning) "Pauza" else "Wznów")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { stopwatchViewModel.reset() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Zakończ")
                        }
                    } else {
                        Button(
                            onClick = { 
                                stopwatchViewModel.start(trail.id, trail.name)
                            }
                        ) {
                            Text(if (currentStopwatchTrailId != null) "Uruchom tutaj (zastąp obecny)" else "Start")
                        }
                    }
                }
            }
        }
    }
}

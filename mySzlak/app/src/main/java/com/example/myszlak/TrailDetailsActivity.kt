package com.example.myszlak

import coil.compose.AsyncImage
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.myszlak.TrailViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext

class TrailDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val trailId = intent.getIntExtra("selectedTrail", -1)

        setContent {
            MySzlakTheme {
                val trailViewModel: TrailViewModel = viewModel()
                val stopwatchViewModel: StopwatchViewModel = viewModel()
                val selectedTrail by trailViewModel.selectedTrail.collectAsState()
                val isLoading by trailViewModel.isLoading.collectAsState()
                val errorMessage by trailViewModel.errorMessage.collectAsState()
                var imageLoading by remember { mutableStateOf(true) }
                
                val currentStopwatchTrailId by stopwatchViewModel.currentTrailId.collectAsState()
                val isStopwatchRunning by stopwatchViewModel.isRunning.collectAsState()
                val elapsedSeconds by stopwatchViewModel.elapsedSeconds.collectAsState()

                val context = LocalContext.current

                LaunchedEffect(trailId) {
                    stopwatchViewModel.bindService(context)
                    if (trailId > 2000) {
                        trailViewModel.loadCyclingTrail(trailId)
                    } else {
                        trailViewModel.loadWalkingTrail(trailId)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = { StopwatchFab(stopwatchViewModel) }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                        selectedTrail?.let { trail ->
                            Column(modifier = Modifier.padding(16.dp)) {
                                Box {
                                    AsyncImage(
                                        model = "https://mapi.kaleszynski.xyz/img/${trail.imageId}",
                                        contentDescription = trail.name,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
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
                                
                                // Stopwatch controls for this specific trail
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
                                                        else stopwatchViewModel.start(trail.id) 
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
                                                        stopwatchViewModel.start(trail.id)
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
                    }
                }
            }
        }
    }
}

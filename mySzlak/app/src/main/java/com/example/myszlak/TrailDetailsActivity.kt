package com.example.myszlak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme

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
                            TrailDetailsView(
                                trail = trail,
                                stopwatchViewModel = stopwatchViewModel,
                                trailViewModel = trailViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

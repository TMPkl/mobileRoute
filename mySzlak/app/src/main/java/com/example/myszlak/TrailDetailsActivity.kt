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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.myszlak.TrailViewModel
import com.example.myszlak.Trail
import  androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class TrailDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val trailId = intent.getIntExtra("selectedTrail", -1)

        setContent {
            MySzlakTheme {
                val trailViewModel: TrailViewModel = viewModel()
                val selectedTrail by trailViewModel.selectedTrail.collectAsState()
                val isLoading by trailViewModel.isLoading.collectAsState()
                val errorMessage by trailViewModel.errorMessage.collectAsState()
                var imageLoading by remember { mutableStateOf(true) }

                LaunchedEffect(trailId) {
                    // Porównujemy trailId (Int), a nie selectedTrail (obiekt Trail)
                    if (trailId > 2000) {
                        trailViewModel.loadCyclingTrail(trailId)
                    } else {
                        trailViewModel.loadWalkingTrail(trailId)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                                Text(
                                    text = "tutaj bedzie stoper"
                                )
                            }
                        }

                    }
                }
            }

    }
}}
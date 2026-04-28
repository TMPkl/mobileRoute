package com.example.myszlak

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopwatchFab(
    viewModel: StopwatchViewModel = viewModel()
) {
    val context = LocalContext.current
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentTrailId by viewModel.currentTrailId.collectAsState()
    val currentTrailName by viewModel.currentTrailName.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.bindService(context)
    }

    // Only show FAB if a stopwatch is active for some trail
    if (currentTrailId != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            LargeFloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = formatTime(elapsedSeconds),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 24.sp
                )
            }
        }
    }

    // Bottom Sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentTrailName ?: "Trasa",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatTime(elapsedSeconds),
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { 
                        if (isRunning) viewModel.pause() 
                        else currentTrailId?.let { id -> 
                            viewModel.start(id, currentTrailName ?: "Trasa") 
                        }
                    }) {
                        Text(text = if (isRunning) "Pauza" else "Wznów")
                    }
                    OutlinedButton(onClick = { 
                        viewModel.reset()
                        showSheet = false
                    }) {
                        Text(text = "Zatrzymaj")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) {
        "%02d:%02d:%02d".format(h, m, s)
    } else {
        "%02d:%02d".format(m, s)
    }
}

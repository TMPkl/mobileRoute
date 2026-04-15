package com.example.myszlak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MySzlakTheme {
                val viewModel: BaseViewModel = viewModel()
                var visible by remember { mutableStateOf(false) }
                var all_loaded by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(400)
                    visible = true
                    delay(1500)
                    all_loaded = true
                }

                LaunchedEffect(all_loaded) {
                    if (all_loaded) {
                        val intent = Intent(this@MainActivity, RoutesActivity::class.java).apply {
                            putExtra("activityType", "rowerowe")
                        }
                        startActivity(intent)
                        // Animacja przejścia (fade in/out)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }

                val size by animateDpAsState(
                    targetValue = if (visible) 400.dp else 1.dp,
                    label = "logoSize"
                )
                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    label = "textAlpha"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        StopwatchFab()
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(200.dp))
                        Text(
                            text = "Witaj w aplikacji MySzlak!",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.graphicsLayer(alpha = alpha)
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(size)
                        )
                    }
                }
            }
        }
    }
}

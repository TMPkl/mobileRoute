package com.example.myszlak

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myszlak.ui.theme.MySzlakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MySzlakTheme {
                // Inicjalizacja ViewModelu wewnątrz setContent
                val viewModel: BaseViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Wybierz rodzaj aktywności",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal
                        )

                        // Przykład reaktywnego tekstu z ViewModelu
//                        if (viewModel.activityType.isNotEmpty()) {
//                            Text(text = "Wybrano: ${viewModel.activityType}", color = androidx.compose.ui.graphics.Color.Gray)
//                        }

                        Spacer(modifier = Modifier.height(200.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // PRZYCISK: ROWER
                            Button(
                                onClick = {
                                    viewModel.activityType = "rowerowe"
                                    navigateToRoutes(viewModel.activityType)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(text = "trasy rowerowe", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            // PRZYCISK: PIESZE
                            Button(
                                onClick = {
                                    viewModel.activityType = "piesze"
                                    navigateToRoutes(viewModel.activityType)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(text = "trasy piesze", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Pomocnicza funkcja do nawigacji
    private fun navigateToRoutes(type: String) {
        val intent = Intent(this, RoutesActivity::class.java).apply {
            putExtra("activityType", type)
        }
        startActivity(intent)
    }
}
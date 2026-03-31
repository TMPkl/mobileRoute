package com.example.myszlak

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    // Dzięki 'by' i 'mutableStateOf' Compose wie, kiedy odświeżyć UI
    var activityType by mutableStateOf("")
}
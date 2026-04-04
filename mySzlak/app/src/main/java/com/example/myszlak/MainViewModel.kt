package com.example.myszlak

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    var activityType by mutableStateOf("")
}
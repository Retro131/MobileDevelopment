package com.example.mobiledevelopment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.mobiledevelopment.registration.RegistrationConfig
import com.example.mobiledevelopment.registration.RegistrationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {MaterialTheme {
            val config = remember { RegistrationConfig() }
            Surface(modifier = Modifier.fillMaxSize()) {
                RegistrationScreen(config)
            }
        }
        }
    }
}
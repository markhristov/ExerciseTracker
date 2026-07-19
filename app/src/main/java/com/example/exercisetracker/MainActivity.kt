package com.example.exercisetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.exercisetracker.ui.ExerciseTrackerApp
import com.example.exercisetracker.ui.theme.ExerciseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExerciseTrackerTheme {
                Surface (modifier = Modifier.fillMaxSize()) {
                    ExerciseTrackerApp(

                    )
                }
            }
        }
    }
}

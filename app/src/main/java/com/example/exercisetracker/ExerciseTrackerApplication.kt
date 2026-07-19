package com.example.exercisetracker

import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.example.exercisetracker.data.AppContainer
import com.example.exercisetracker.data.DefaultAppContainer

class ExerciseTrackerApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}

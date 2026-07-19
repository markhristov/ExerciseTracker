package com.example.exercisetracker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExerciseTrackerApp(
    viewModel: PoseViewModel = viewModel(factory = PoseViewModel.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        AppBar()
    }) { paddingValues ->

        ExerciseScreen(
            uiState = uiState.value,
            onStartClick = { viewModel.toggleWorkout() },
            onCameraFrame = { viewModel.detectPoses(it) },

            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Exercise tracker app") },
        modifier = modifier
    )
}
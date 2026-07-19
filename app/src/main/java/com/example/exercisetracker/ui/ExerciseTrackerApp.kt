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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exercisetracker.PoseViewModel

@Composable
fun ExerciseTrackerApp(
    viewModel: PoseViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(topBar = {
        AppBar()
    }) { paddingValues ->

        ExerciseScreen(
            uiState = uiState.value,
            onStartClick = { viewModel.toggleWorkout() },
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
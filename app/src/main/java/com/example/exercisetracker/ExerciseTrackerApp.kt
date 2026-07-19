package com.example.exercisetracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exercisetracker.ui.theme.ExerciseTrackerTheme
import java.nio.file.WatchEvent

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

@Composable
fun ExerciseScreen(uiState: PoseUiState, onStartClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center) {
        CameraPreview(modifier = Modifier.weight(1f))
        Text(text = "Reps: ${uiState.repCount}",
            modifier = Modifier.padding(16.dp))
        Button(onClick = onStartClick) {
            Text(text = if (uiState.isRecording) "Stop" else "Start",
                modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(color = Color.Gray)) {

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

@Preview
@Composable
fun ExerciseScreenPreview() {
    ExerciseTrackerTheme() {
        ExerciseScreen(PoseUiState(), {},
            modifier = Modifier.fillMaxSize())
    }
}
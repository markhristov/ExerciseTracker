package com.example.exercisetracker.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.exercisetracker.PoseUiState
import com.example.exercisetracker.ui.theme.ExerciseTrackerTheme

@Composable
fun ExerciseScreen(uiState: PoseUiState, onStartClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val hasCameraPermission =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            onStartClick()
        }
    }
    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center) {
        CameraPreview(modifier = Modifier.weight(1f))
        Text(text = "Reps: ${uiState.repCount}",
            modifier = Modifier.padding(16.dp))
        Button(onClick = {
            if (hasCameraPermission) {
                onStartClick()
            } else {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }) {
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

@Preview
@Composable
fun ExerciseScreenPreview() {
    ExerciseTrackerTheme() {
        ExerciseScreen(
            PoseUiState(), {},
            modifier = Modifier.fillMaxSize())
    }
}
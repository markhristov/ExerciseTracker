package com.example.exercisetracker.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.exercisetracker.PoseUiState
import com.example.exercisetracker.camera.CameraManager
import com.example.exercisetracker.ui.theme.ExerciseTrackerTheme

@Composable
fun ExerciseScreen(
    uiState: PoseUiState,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCameraFrame: (ImageProxy) -> Unit = { imageProxy -> imageProxy.close() }
) {
    val context = LocalContext.current
    val hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var cameraPermissionGranted by remember { mutableStateOf(hasCameraPermission) }
    val canShowCamera =
        cameraPermissionGranted ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted = granted
        if (granted) {
            onStartClick()
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (canShowCamera) {
                CameraPreview(
                    isActive = uiState.isRecording,
                    onFrame = onCameraFrame,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "Camera permission is needed to preview and count push-ups.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Text(
            text = "Reps: ${uiState.repCount}",
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = {
            if (canShowCamera) {
                onStartClick()
            } else {
                launcher.launch(Manifest.permission.CAMERA)
            }
        }) {
            Text(
                text = if (uiState.isRecording) "Stop" else "Start",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CameraPreview(
    isActive: Boolean,
    onFrame: (ImageProxy) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember(context) {
        CameraManager(context.applicationContext)
    }
    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(isActive, lifecycleOwner, previewView) {
        if (isActive) {
            cameraManager.startCamera(
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                onFrame = onFrame
            )
        } else {
            cameraManager.stopCamera()
        }

        onDispose {
            cameraManager.stopCamera()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
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

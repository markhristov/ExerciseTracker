package com.example.exercisetracker.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.exercisetracker.camera.CameraManager
import com.example.exercisetracker.exercise.DetectionMode
import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.ui.PoseUiState
import com.example.exercisetracker.ui.theme.ExerciseTrackerTheme

@Composable
fun CameraPermissionGate(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    if (hasPermission) {
        content()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Camera permission is required.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant permission")
                }
            }
        }
    }
}

@Composable
fun ExerciseScreen(
    uiState: PoseUiState,
    onButtonClick: () -> Unit,
    onChangeExercise: (ExerciseType) -> Unit,
    onDetectionModeChanged: (DetectionMode) -> Unit,
    modifier: Modifier = Modifier,
    onCameraFrame: (ImageProxy) -> Unit = { imageProxy -> imageProxy.close() },
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            CameraPermissionGate {
                CameraPreview(
                    enabled = uiState.isRecording,
                    onFrame = onCameraFrame,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Row(modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Reps: ${uiState.repCount}",
                modifier = Modifier.padding(top = 12.dp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(16.dp))

            ExerciseDropdown(
                selected = uiState.exerciseType,
                onSelected = { onChangeExercise(it) },
                onDetectionModeChanged = onDetectionModeChanged,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
        Button(
            onClick = {
                onButtonClick()
            }, modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = if (uiState.isRecording) "Stop" else "Start",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDropdown(
    selected: ExerciseType,
    onSelected: (ExerciseType) -> Unit,
    modifier: Modifier = Modifier,
    onDetectionModeChanged: (DetectionMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.name.replace('_', ' '),
            onValueChange = {},
            readOnly = true,
            label = { Text("Exercise") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(
                text = { Text("Automatic") },
                onClick = {
                    expanded = false
                    onDetectionModeChanged(DetectionMode.AUTOMATIC)
                }
            )

            ExerciseType.entries.forEach { exercise ->
                DropdownMenuItem(
                    text = {
                        Text(exercise.name.replace('_', ' '))
                    },
                    onClick = {
                        onSelected(exercise)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun CameraPreview(
    enabled: Boolean, onFrame: (ImageProxy) -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraManager = remember(context) {
        CameraManager(context.applicationContext)
    }
    val previewView = remember(context) {
        createPreviewView(context)
    }
    val currentOnFrame by rememberUpdatedState(onFrame)

    DisposableEffect(enabled, lifecycleOwner, previewView) {
        if (enabled) {
            cameraManager.startCamera(
                previewView = previewView, lifecycleOwner = lifecycleOwner, onFrame = currentOnFrame
            )
        } else {
            cameraManager.stopCamera()
        }

        onDispose {
            cameraManager.stopCamera()
        }
    }

    if (enabled) {
        AndroidView(
            factory = { previewView }, modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color.Gray)
        )
    }

}

private fun createPreviewView(context: Context) = PreviewView(context).apply {
    scaleType = PreviewView.ScaleType.FILL_CENTER
}

@Preview
@Composable
fun ExerciseScreenPreview() {
    ExerciseTrackerTheme {
        ExerciseScreen(
            PoseUiState(), {},{}, {}, modifier = Modifier.fillMaxSize(),
        )
    }
}

package com.example.exercisetracker.ui

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.exercisetracker.ExerciseTrackerApplication
import com.example.exercisetracker.PoseLandmarkerHelper
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


private const val TAG = "PoseViewModel"


class PoseViewModel(
    val poseLandmarkerHelper: PoseLandmarkerHelper,
    val poseDetector: PoseDetector
) : ViewModel(), PoseDetectionListener {
    private val _uiState = MutableStateFlow(PoseUiState())
    val uiState = _uiState

    init {
        poseDetector.listener = this
        poseLandmarkerHelper.setupPoseLandmarker()
    }

    fun detectPoses(imageProxy: ImageProxy) {
        poseLandmarkerHelper.detectLiveStream(imageProxy, true)
    }

    fun toggleWorkout() {
        if (_uiState.value.isRecording) {
            stopWorkout()
        } else {
            startWorkout()
        }
    }

    fun startWorkout() {
        _uiState.update {
            it.copy(isRecording = true)
        }
    }

    fun stopWorkout() {
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    fun onRepDetected() {
        _uiState.update {
            it.copy(
                repCount = it.repCount + 1
            )
        }
    }

    override fun onDetection(result: DetectionDetails) {
        if (result.repCompleted) {
            onRepDetected()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ExerciseTrackerApplication)
                val poseLandmarkerHelper = application.container.poseLandmarkerHelper
                val poseDetector = application.container.poseDetector
                PoseViewModel(
                    poseLandmarkerHelper = poseLandmarkerHelper,
                    poseDetector = poseDetector
                )
            }
        }
    }

}




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
import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.ExerciseDetectorFactory
import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.exercise.PushUpDetectionResult
import com.example.exercisetracker.pose.BodyPose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "PoseViewModel"

class PoseViewModel(
    val poseLandmarkerHelper: PoseLandmarkerHelper,
    val detectorFactory: ExerciseDetectorFactory
) : ViewModel(), PoseLandmarkerHelper.LandmarkerListener {
    private val _uiState = MutableStateFlow(PoseUiState())
    val uiState = _uiState
    private var detector: ExerciseDetector = detectorFactory.create(_uiState.value.exerciseType)

    init {
        poseLandmarkerHelper.poseLandmarkerListener = this
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
            it.copy(
                repCount = 0, isRecording = true
            )
        }
    }

    fun stopWorkout() {
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    fun changeExercise(type: ExerciseType) {
        _uiState.update {
            it.copy(
                exerciseType = type,
                repCount = 0,
                isRecording = false
            )
        }

        detector = detectorFactory.create(type)
    }

    fun onRepDetected() {
        _uiState.update {
            it.copy(
                repCount = it.repCount + 1
            )
        }
    }

    override fun onError(error: String, errorCode: Int) {
        Log.d(TAG, "An error: $error with code $errorCode occurred")
    }

    override fun onResults(bodyPose: BodyPose) {
        when (val result = detector.process(bodyPose)) {
            is PushUpDetectionResult -> if (result.repCompleted) onRepDetected() else {
            }

            else -> {}
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ExerciseTrackerApplication)
                val poseLandmarker = application.container.poseLandmarkerHelper
                val detectorFactory = application.container.detectorFactory
                PoseViewModel(
                    poseLandmarkerHelper = poseLandmarker, detectorFactory = detectorFactory
                )
            }
        }
    }

}




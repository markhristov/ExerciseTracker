package com.example.exercisetracker.ui

import com.example.exercisetracker.exercise.DetectionMode
import com.example.exercisetracker.exercise.ExerciseType

data class PoseUiState(
    val isRecording: Boolean = false,
    val repCount: Int = 0,
    val selectedExercise: ExerciseType = ExerciseType.PUSH_UP,
    val detectionMode: DetectionMode = DetectionMode.MANUAL,
    val detectedExercise: ExerciseType? = null
)
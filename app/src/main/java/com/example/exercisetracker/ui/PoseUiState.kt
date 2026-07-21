package com.example.exercisetracker.ui

import com.example.exercisetracker.exercise.DetectionMode
import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.exercise.pushup.PushUpDetector

data class PoseUiState(
    val isRecording: Boolean = false,
    val repCount: Int = 0,
    val detectionMode: DetectionMode = DetectionMode.MANUAL,
    val exerciseType: ExerciseType = ExerciseType.PUSH_UP
)
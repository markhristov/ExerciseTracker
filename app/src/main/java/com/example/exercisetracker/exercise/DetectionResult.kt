package com.example.exercisetracker.exercise

import com.example.exercisetracker.exercise.pushup.PushUpState
import com.example.exercisetracker.exercise.squat.SquatState

sealed interface DetectionResult

data class PushUpDetectionResult(
    val state: PushUpState,
    val elbowAngle: Double,
    val repCompleted: Boolean
) : DetectionResult

data class SquatDetectionResult(
    val state: SquatState,
    val kneeAngle: Double,
    val repCompleted: Boolean
) : DetectionResult

data object NoVisibleBodyPart : DetectionResult
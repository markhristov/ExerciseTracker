package com.example.exercisetracker.exercise

import com.example.exercisetracker.exercise.pushup.PushUpState

sealed interface DetectionResult

data class DetectionDetails(
    val pushUpState: PushUpState,
    val elbowAngle: Double,
    val repCompleted: Boolean
) : DetectionResult

data object NoVisibleArm : DetectionResult

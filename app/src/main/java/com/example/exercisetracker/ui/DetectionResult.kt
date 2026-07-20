package com.example.exercisetracker.ui

import com.example.exercisetracker.model.Stage

sealed interface DetectionResult

data class DetectionDetails(
    val stage: Stage,
    val elbowAngle: Double,
    val repCompleted: Boolean
) : DetectionResult

data object NoVisibleArm : DetectionResult

package com.example.exercisetracker.ui

import com.example.exercisetracker.model.Stage

data class DetectionResult(
    val stage: Stage,
    val elbowAngle: Double,
    val repCompleted: Boolean
)

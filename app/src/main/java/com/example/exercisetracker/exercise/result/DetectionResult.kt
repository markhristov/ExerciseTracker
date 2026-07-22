package com.example.exercisetracker.exercise.result

import com.example.exercisetracker.exercise.pushup.PushUpState
import com.example.exercisetracker.exercise.squat.SquatState

sealed interface DetectionResult

data class PushUpDetectionResult(
    val state: PushUpState,
    val elbowAngle: Double,
    override val repCompleted: Boolean
) : DetectionResult, RepDetectionResult

data class SquatDetectionResult(
    val state: SquatState,
    val kneeAngle: Double,
    override val repCompleted: Boolean
) : DetectionResult, RepDetectionResult

data object NoVisibleBodyPart : DetectionResult
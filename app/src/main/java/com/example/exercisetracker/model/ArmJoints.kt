package com.example.exercisetracker.model

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

data class ArmsJoints(
    val leftShoulder: NormalizedLandmark,
    val leftElbow: NormalizedLandmark,
    val leftWrist: NormalizedLandmark,
    val rightShoulder: NormalizedLandmark,
    val rightElbow: NormalizedLandmark,
    val rightWrist: NormalizedLandmark
)
package com.example.exercisetracker.model

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

data class Arm(
    val shoulder: NormalizedLandmark,
    val elbow: NormalizedLandmark,
    val wrist:NormalizedLandmark
)

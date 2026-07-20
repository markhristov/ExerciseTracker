package com.example.exercisetracker.ui.extensions

import com.example.exercisetracker.model.ArmsJoints
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

fun List<NormalizedLandmark>.toArmJoints(): ArmsJoints {
    return ArmsJoints(
        leftShoulder = this[11],
        leftElbow = this[13],
        leftWrist = this[15],
        rightShoulder = this[12],
        rightElbow = this[14],
        rightWrist = this[16]
    )
}

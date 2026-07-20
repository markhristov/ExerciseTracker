package com.example.exercisetracker.data

import android.content.Context
import com.example.exercisetracker.PoseLandmarkerHelper
import com.example.exercisetracker.ui.PoseDetector

interface AppContainer {
    val poseLandmarkerHelper: PoseLandmarkerHelper
    val poseDetector: PoseDetector
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val poseDetector: PoseDetector = PoseDetector()
    override var poseLandmarkerHelper =
        PoseLandmarkerHelper(context = context, poseLandmarkerHelperListener = poseDetector)
}
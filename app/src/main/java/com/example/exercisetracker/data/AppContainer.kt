package com.example.exercisetracker.data

import android.content.Context
import com.example.exercisetracker.PoseLandmarkerHelper
import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.pushup.PushUpDetector

interface AppContainer {
    val poseLandmarkerHelper: PoseLandmarkerHelper
    val poseDetector: ExerciseDetector
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val poseDetector: ExerciseDetector = PushUpDetector()
    override var poseLandmarkerHelper =
        PoseLandmarkerHelper(context = context)
}
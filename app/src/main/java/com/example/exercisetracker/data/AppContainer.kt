package com.example.exercisetracker.data

import android.content.Context
import com.example.exercisetracker.PoseLandmarkerHelper
import com.example.exercisetracker.exercise.DefaultExerciseDetectorFactory
import com.example.exercisetracker.exercise.ExerciseDetectorFactory

interface AppContainer {
    val poseLandmarkerHelper: PoseLandmarkerHelper
    val detectorFactory: ExerciseDetectorFactory
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val detectorFactory: ExerciseDetectorFactory = DefaultExerciseDetectorFactory()
    override var poseLandmarkerHelper =
        PoseLandmarkerHelper(context = context)
}
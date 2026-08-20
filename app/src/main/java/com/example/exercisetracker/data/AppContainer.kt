package com.example.exercisetracker.data

import android.content.Context
import com.example.exercisetracker.pose.PoseLandmarkerHelper
import com.example.exercisetracker.exercise.DefaultExerciseDetectorFactory
import com.example.exercisetracker.exercise.ExerciseDetectorFactory
import com.example.exercisetracker.exercise.classifier.DefaultExerciseClassifier
import com.example.exercisetracker.exercise.classifier.ExerciseClassifier

interface AppContainer {
    val poseLandmarkerHelper: PoseLandmarkerHelper
    val detectorFactory: ExerciseDetectorFactory
    val exerciseClassifier: ExerciseClassifier
}

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    override val detectorFactory: ExerciseDetectorFactory = DefaultExerciseDetectorFactory()
    override var poseLandmarkerHelper =
        PoseLandmarkerHelper(context = context)
    override val exerciseClassifier: ExerciseClassifier = DefaultExerciseClassifier()
}
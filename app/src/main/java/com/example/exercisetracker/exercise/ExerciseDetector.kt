package com.example.exercisetracker.exercise

import com.example.exercisetracker.ui.PoseDetectionListener
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

interface ExerciseDetector {
    var listener: PoseDetectionListener?
    fun process(
        result: PoseLandmarkerResult
    ): DetectionResult
}
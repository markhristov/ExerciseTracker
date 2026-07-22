package com.example.exercisetracker.exercise

import com.example.exercisetracker.exercise.result.DetectionResult
import com.example.exercisetracker.pose.BodyPose

interface ExerciseDetector {
    fun process(
        bodyPose: BodyPose
    ): DetectionResult
}
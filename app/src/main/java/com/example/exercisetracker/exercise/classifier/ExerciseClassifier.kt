package com.example.exercisetracker.exercise.classifier

import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.pose.BodyPose

interface ExerciseClassifier {
    fun classify(bodyPose: BodyPose): ExerciseType
}
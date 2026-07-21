package com.example.exercisetracker.exercise.classifier

import com.example.exercisetracker.exercise.ExerciseType
import com.example.exercisetracker.pose.BodyPose
import com.example.exercisetracker.pose.isHorizontal

class DefaultExerciseClassifier : ExerciseClassifier{
    override fun classify(bodyPose: BodyPose): ExerciseType =
        if (bodyPose.isHorizontal()) {
            ExerciseType.PUSH_UP
        } else {
            ExerciseType.SQUAT
        }
}
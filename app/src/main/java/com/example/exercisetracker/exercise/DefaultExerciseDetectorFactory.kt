package com.example.exercisetracker.exercise

import com.example.exercisetracker.exercise.pushup.PushUpDetector
import com.example.exercisetracker.exercise.squat.SquatDetector

class DefaultExerciseDetectorFactory : ExerciseDetectorFactory {

    override fun create(type: ExerciseType): ExerciseDetector =
        when (type) {
            ExerciseType.PUSH_UP -> PushUpDetector()
            ExerciseType.SQUAT -> SquatDetector()
        }
}
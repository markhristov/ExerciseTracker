package com.example.exercisetracker.exercise

interface ExerciseDetectorFactory {
    fun create(type: ExerciseType): ExerciseDetector
}
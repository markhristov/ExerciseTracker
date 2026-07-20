package com.example.exercisetracker.ui

import com.example.exercisetracker.exercise.DetectionResult

interface PoseDetectionListener {
    fun onDetection(result: DetectionResult)
}

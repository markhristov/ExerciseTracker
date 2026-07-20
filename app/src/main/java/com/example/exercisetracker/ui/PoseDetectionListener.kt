package com.example.exercisetracker.ui

import com.example.exercisetracker.exercise.DetectionDetails

interface PoseDetectionListener {
    fun onDetection(result: DetectionDetails)
}

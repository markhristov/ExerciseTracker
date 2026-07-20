package com.example.exercisetracker.model

import com.example.exercisetracker.pose.Joint

data class Arm(
    val shoulder: Joint,
    val elbow: Joint,
    val wrist: Joint
)

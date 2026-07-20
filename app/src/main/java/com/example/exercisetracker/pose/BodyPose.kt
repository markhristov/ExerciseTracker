package com.example.exercisetracker.pose

data class BodyPose(
    val leftShoulder: Joint,
    val leftElbow: Joint,
    val leftWrist: Joint,
)

data class Joint(
    val x: Double,
    val y: Double,
)
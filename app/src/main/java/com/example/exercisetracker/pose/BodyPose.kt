package com.example.exercisetracker.pose

import com.example.exercisetracker.model.Arm

data class BodyPose(

    val nose: Joint,

    val leftShoulder: Joint,
    val rightShoulder: Joint,

    val leftElbow: Joint,
    val rightElbow: Joint,

    val leftWrist: Joint,
    val rightWrist: Joint,

    val leftHip: Joint,
    val rightHip: Joint,

    val leftKnee: Joint,
    val rightKnee: Joint,

    val leftAnkle: Joint,
    val rightAnkle: Joint,
) {
    val leftArm: Arm
        get() = Arm(leftShoulder, leftElbow, leftWrist)

    val rightArm: Arm
        get() = Arm(rightShoulder, rightElbow, rightWrist)
}

data class Joint(
    val x: Float,
    val y: Float,
    val visibility: Float
)
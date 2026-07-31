package com.example.exercisetracker.pose

import kotlin.math.cos
import kotlin.math.sin

internal fun testPose(visibility: Float = 1f): BodyPose {
    val joint = Joint(x = 0f, y = 0f, visibility = visibility)
    return BodyPose(
        nose = joint,
        leftShoulder = joint,
        rightShoulder = joint,
        leftElbow = joint,
        rightElbow = joint,
        leftWrist = joint,
        rightWrist = joint,
        leftHip = joint,
        rightHip = joint,
        leftKnee = joint,
        rightKnee = joint,
        leftAnkle = joint,
        rightAnkle = joint
    )
}

internal fun poseWithElbowAngle(
    angleDegrees: Double,
    visibility: Float = 1f
): BodyPose {
    val vertex = Joint(0f, 0f, visibility)
    val first = Joint(1f, 0f, visibility)
    val radians = Math.toRadians(angleDegrees)
    val third = Joint(cos(radians).toFloat(), sin(radians).toFloat(), visibility)

    return testPose(visibility).copy(
        leftShoulder = first,
        rightShoulder = first,
        leftElbow = vertex,
        rightElbow = vertex,
        leftWrist = third,
        rightWrist = third
    )
}

internal fun poseWithKneeAngle(
    angleDegrees: Double,
    visibility: Float = 1f
): BodyPose {
    val vertex = Joint(0f, 0f, visibility)
    val first = Joint(1f, 0f, visibility)
    val radians = Math.toRadians(angleDegrees)
    val third = Joint(cos(radians).toFloat(), sin(radians).toFloat(), visibility)

    return testPose(visibility).copy(
        leftHip = first,
        rightHip = first,
        leftKnee = vertex,
        rightKnee = vertex,
        leftAnkle = third,
        rightAnkle = third
    )
}

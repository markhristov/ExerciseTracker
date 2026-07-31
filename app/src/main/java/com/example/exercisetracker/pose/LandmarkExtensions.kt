package com.example.exercisetracker.pose

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2

fun NormalizedLandmark.toJoint() = Joint(
    x = x(), y = y(), visibility = visibility().orElse(0f)
)

fun List<NormalizedLandmark>.toBodyPose(): BodyPose {
    require(size >= 29) {
        "Expected at least 29 landmarks, but got $size."
    }

    return BodyPose(
        nose = this[0].toJoint(),

        leftShoulder = this[11].toJoint(), rightShoulder = this[12].toJoint(),
        leftElbow = this[13].toJoint(), rightElbow = this[14].toJoint(),
        leftWrist = this[15].toJoint(), rightWrist = this[16].toJoint(),
        leftHip = this[23].toJoint(), rightHip = this[24].toJoint(),
        leftKnee = this[25].toJoint(), rightKnee = this[26].toJoint(),
        leftAnkle = this[27].toJoint(), rightAnkle = this[28].toJoint()
    )
}


fun BodyPose.torsoAngle(): Double {

    val shoulderCenterX = (leftShoulder.x + rightShoulder.x) / 2f
    val shoulderCenterY = (leftShoulder.y + rightShoulder.y) / 2f

    val hipCenterX = (leftHip.x + rightHip.x) / 2f
    val hipCenterY = (leftHip.y + rightHip.y) / 2f

    val angle = Math.toDegrees(
        atan2(
            (hipCenterY - shoulderCenterY).toDouble(), (hipCenterX - shoulderCenterX).toDouble()
        )
    )

    return angle
}

fun BodyPose.isHorizontal(
    thresholdDegrees: Double = 30.0
): Boolean {
    val angle = abs(torsoAngle())

    val horizontalAngle = minOf(angle, 180.0 - angle)

    return horizontalAngle < thresholdDegrees
}
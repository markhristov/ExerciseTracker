package com.example.exercisetracker.pose

import android.R.attr.visibility
import android.util.Log
import com.example.exercisetracker.model.Arm
import kotlin.math.acos
import kotlin.math.sqrt

private const val TAG = "PoseMath"

fun getBestVisibleArm(bodyPose: BodyPose, visibilityThreshold: Float = 0.7f): Arm? {
    val leftArm = bodyPose.leftArm
    val rightArm = bodyPose.rightArm

    val leftVisibility = minOf(
        leftArm.shoulder.visibility,
        leftArm.elbow.visibility,
        leftArm.wrist.visibility
    )

    val rightVisibility = minOf(
        rightArm.shoulder.visibility,
        rightArm.elbow.visibility,
        rightArm.wrist.visibility
    )
    val bestVisibility = maxOf(leftVisibility, rightVisibility)

    if (bestVisibility < visibilityThreshold) {
        Log.d(TAG, "Visibility ${bestVisibility * 100}%")
        return null
    }

    return if (leftVisibility >= rightVisibility) {
        leftArm
    } else {
        rightArm
    }
}


fun calculateElbowAngle(arm: Arm): Double {
    val shoulderX = arm.shoulder.x.toDouble()
    val shoulderY = arm.shoulder.y.toDouble()

    val elbowX = arm.elbow.x.toDouble()
    val elbowY = arm.elbow.y.toDouble()

    val wristX = arm.wrist.x.toDouble()
    val wristY = arm.wrist.y.toDouble()

    val v1x = shoulderX - elbowX
    val v1y = shoulderY - elbowY

    val v2x = wristX - elbowX
    val v2y = wristY - elbowY

    val dot = v1x * v2x + v1y * v2y

    val mag1 = sqrt(v1x * v1x + v1y * v1y)
    val mag2 = sqrt(v2x * v2x + v2y * v2y)

    val cosTheta = (dot / (mag1 * mag2)).coerceIn(-1.0, 1.0)

    return Math.toDegrees(acos(cosTheta))
}
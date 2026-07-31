package com.example.exercisetracker.pose

import com.example.exercisetracker.model.Arm
import com.example.exercisetracker.model.Leg
import kotlin.math.acos
import kotlin.math.sqrt

fun Leg.isStanding(): Boolean =
    calculateAngle(hip, knee, ankle) > 165

fun Leg.isSquatting(): Boolean =
    calculateAngle(hip, knee, ankle) < 100

fun calculateAngle(
    first: Joint,
    vertex: Joint,
    third: Joint
): Double {
    val firstX = first.x.toDouble()
    val firstY = first.y.toDouble()

    val vertexX = vertex.x.toDouble()
    val vertexY = vertex.y.toDouble()

    val thirdX = third.x.toDouble()
    val thirdY = third.y.toDouble()

    val v1x = firstX - vertexX
    val v1y = firstY - vertexY

    val v2x = thirdX - vertexX
    val v2y = thirdY - vertexY

    val dot = v1x * v2x + v1y * v2y

    val mag1 = sqrt(v1x * v1x + v1y * v1y)
    val mag2 = sqrt(v2x * v2x + v2y * v2y)

    if (mag1 == 0.0 || mag2 == 0.0) {
        return Double.NaN
    }

    val cosTheta = (dot / (mag1 * mag2)).coerceIn(-1.0, 1.0)

    return Math.toDegrees(acos(cosTheta))
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
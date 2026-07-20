package com.example.exercisetracker.pose

import com.example.exercisetracker.model.Arm
import com.example.exercisetracker.model.Leg

fun BodyPose.bestVisibleArm(visibilityThreshold: Float = 0.7f): Arm? {
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
        return null
    }

    return if (leftVisibility >= rightVisibility)
        leftArm
    else
        rightArm
}


fun BodyPose.bestVisibleLeg(
    visibilityThreshold: Float = 0.7f
): Leg? {

    val leftVisibility = minOf(
        leftLeg.hip.visibility,
        leftLeg.knee.visibility,
        leftLeg.ankle.visibility
    )

    val rightVisibility = minOf(
        rightLeg.hip.visibility,
        rightLeg.knee.visibility,
        rightLeg.ankle.visibility
    )

    val bestVisibility = maxOf(leftVisibility, rightVisibility)

    if (bestVisibility < visibilityThreshold) {
        return null
    }

    return if (leftVisibility >= rightVisibility) leftLeg else rightLeg
}

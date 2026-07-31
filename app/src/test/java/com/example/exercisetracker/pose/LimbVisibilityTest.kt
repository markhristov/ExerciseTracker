package com.example.exercisetracker.pose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LimbVisibilityTest {
    @Test
    fun bestVisibleArm_rightArmHasHigherMinimumVisibility_returnsRightArm() {
        val pose = testPose().copy(
            leftShoulder = Joint(0f, 0f, 0.9f),
            leftElbow = Joint(0f, 0f, 0.8f),
            leftWrist = Joint(0f, 0f, 0.7f),
            rightShoulder = Joint(0f, 0f, 0.9f),
            rightElbow = Joint(0f, 0f, 0.9f),
            rightWrist = Joint(0f, 0f, 0.85f)
        )

        assertEquals(pose.rightArm, pose.bestVisibleArm())
    }

    @Test
    fun bestVisibleArm_neitherArmMeetsThreshold_returnsNull() {
        val pose = testPose(visibility = 0.69f)

        assertNull(pose.bestVisibleArm())
    }

    @Test
    fun bestVisibleLeg_rightLegHasHigherMinimumVisibility_returnsRightLeg() {
        val pose = testPose().copy(
            leftHip = Joint(0f, 0f, 0.75f),
            leftKnee = Joint(0f, 0f, 0.8f),
            leftAnkle = Joint(0f, 0f, 0.9f),
            rightHip = Joint(0f, 0f, 0.95f),
            rightKnee = Joint(0f, 0f, 0.9f),
            rightAnkle = Joint(0f, 0f, 0.85f)
        )

        assertEquals(pose.rightLeg, pose.bestVisibleLeg())
    }
}

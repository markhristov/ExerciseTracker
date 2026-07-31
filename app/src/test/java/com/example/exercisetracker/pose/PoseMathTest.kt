package com.example.exercisetracker.pose

import com.example.exercisetracker.model.Leg
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PoseMathTest {
    @Test
    fun calculateAngle_straightLine_returns180Degrees() {
        val angle = calculateAngle(
            first = Joint(-1f, 0f, 1f),
            vertex = Joint(0f, 0f, 1f),
            third = Joint(1f, 0f, 1f)
        )

        assertEquals(180.0, angle, 0.001)
    }

    @Test
    fun calculateAngle_rightAngle_returns90Degrees() {
        val angle = calculateAngle(
            first = Joint(1f, 0f, 1f),
            vertex = Joint(0f, 0f, 1f),
            third = Joint(0f, 1f, 1f)
        )

        assertEquals(90.0, angle, 0.001)
    }

    @Test
    fun calculateAngle_zeroLengthSegment_returnsNaN() {
        val point = Joint(0f, 0f, 1f)

        assertTrue(calculateAngle(point, point, Joint(1f, 0f, 1f)).isNaN())
    }

    @Test
    fun postureHelpers_anglesCrossThresholds_returnExpectedPostures() {
        val standingLeg = Leg(
            hip = Joint(-1f, 0f, 1f),
            knee = Joint(0f, 0f, 1f),
            ankle = Joint(1f, 0f, 1f)
        )
        val squattingLeg = Leg(
            hip = Joint(1f, 0f, 1f),
            knee = Joint(0f, 0f, 1f),
            ankle = Joint(0f, 1f, 1f)
        )

        assertTrue(standingLeg.isStanding())
        assertFalse(standingLeg.isSquatting())
        assertFalse(squattingLeg.isStanding())
        assertTrue(squattingLeg.isSquatting())
    }
}

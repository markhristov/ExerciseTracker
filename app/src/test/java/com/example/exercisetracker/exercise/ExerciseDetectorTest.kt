package com.example.exercisetracker.exercise

import com.example.exercisetracker.exercise.pushup.PushUpDetector
import com.example.exercisetracker.exercise.pushup.PushUpState
import com.example.exercisetracker.exercise.result.NoVisibleBodyPart
import com.example.exercisetracker.exercise.result.PushUpDetectionResult
import com.example.exercisetracker.exercise.result.SquatDetectionResult
import com.example.exercisetracker.exercise.squat.SquatDetector
import com.example.exercisetracker.exercise.squat.SquatState
import com.example.exercisetracker.pose.poseWithElbowAngle
import com.example.exercisetracker.pose.poseWithKneeAngle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseDetectorTest {
    @Test
    fun process_pushUpMovesFromDownToUp_completesRep() {
        val detector = PushUpDetector()

        val initialUp = detector.process(poseWithElbowAngle(175.0)) as PushUpDetectionResult
        val down = detector.process(poseWithElbowAngle(70.0)) as PushUpDetectionResult
        val heldDown = detector.process(poseWithElbowAngle(70.0)) as PushUpDetectionResult
        val completed = detector.process(poseWithElbowAngle(175.0)) as PushUpDetectionResult

        assertEquals(PushUpState.UP, initialUp.state)
        assertFalse(initialUp.repCompleted)
        assertEquals(PushUpState.DOWN, down.state)
        assertFalse(down.repCompleted)
        assertFalse(heldDown.repCompleted)
        assertEquals(PushUpState.UP, completed.state)
        assertTrue(completed.repCompleted)
    }

    @Test
    fun process_poseHasNoVisibleArm_returnsNoVisibleBodyPart() {
        val result = PushUpDetector().process(
            poseWithElbowAngle(angleDegrees = 70.0, visibility = 0.5f)
        )

        assertSame(NoVisibleBodyPart, result)
    }

    @Test
    fun process_squatMovesFromDownToUp_completesRep() {
        val detector = SquatDetector()

        val down = detector.process(poseWithKneeAngle(70.0)) as SquatDetectionResult
        val middle = detector.process(poseWithKneeAngle(120.0)) as SquatDetectionResult
        val completed = detector.process(poseWithKneeAngle(170.0)) as SquatDetectionResult

        assertEquals(SquatState.DOWN, down.state)
        assertFalse(down.repCompleted)
        assertEquals(SquatState.DOWN, middle.state)
        assertFalse(middle.repCompleted)
        assertEquals(SquatState.UP, completed.state)
        assertTrue(completed.repCompleted)
    }
}

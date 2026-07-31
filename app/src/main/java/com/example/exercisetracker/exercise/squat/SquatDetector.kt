package com.example.exercisetracker.exercise.squat

import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.result.DetectionResult
import com.example.exercisetracker.exercise.result.NoVisibleBodyPart
import com.example.exercisetracker.exercise.result.SquatDetectionResult
import com.example.exercisetracker.pose.BodyPose
import com.example.exercisetracker.pose.bestVisibleLeg
import com.example.exercisetracker.pose.calculateAngle

private const val UP_THRESHOLD = 150.0
private const val DOWN_THRESHOLD = 95.0

class SquatDetector : ExerciseDetector {

    private var currentState = SquatState.UP

    override fun process(bodyPose: BodyPose): DetectionResult {
        val leg = bodyPose.bestVisibleLeg() ?: return NoVisibleBodyPart


        val kneeAngle = calculateAngle(
            leg.hip,
            leg.knee,
            leg.ankle
        )

        val newSquatState = when {
            kneeAngle > UP_THRESHOLD -> SquatState.UP
            kneeAngle < DOWN_THRESHOLD -> SquatState.DOWN
            else -> currentState
        }

        var repCompleted = false

        if (currentState == SquatState.DOWN &&
            newSquatState == SquatState.UP
        ) {
            repCompleted = true
        }

        currentState = newSquatState

        return SquatDetectionResult(
            state = newSquatState,
            kneeAngle = kneeAngle,
            repCompleted = repCompleted
        )
    }

    override fun reset() {
        currentState = SquatState.UP
    }
}




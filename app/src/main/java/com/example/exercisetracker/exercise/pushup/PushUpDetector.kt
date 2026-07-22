package com.example.exercisetracker.exercise.pushup

import android.util.Log
import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.result.DetectionResult
import com.example.exercisetracker.exercise.result.NoVisibleBodyPart
import com.example.exercisetracker.exercise.result.PushUpDetectionResult
import com.example.exercisetracker.pose.BodyPose
import com.example.exercisetracker.pose.bestVisibleArm
import com.example.exercisetracker.pose.calculateElbowAngle

private const val TAG = "PoseDetector"
private const val UP_THRESHOLD = 160
private const val DOWN_THRESHOLD = 90

class PushUpDetector : ExerciseDetector {
    private var currentState = PushUpState.UP

    override fun process(bodyPose: BodyPose): DetectionResult {
        Log.i(TAG, bodyPose.toString())
        val arm = bodyPose.bestVisibleArm() ?: return NoVisibleBodyPart
        val elbowAngle = calculateElbowAngle(arm)

        val newPushUpState = when {
            elbowAngle > UP_THRESHOLD -> PushUpState.UP
            elbowAngle < DOWN_THRESHOLD -> PushUpState.DOWN
            else -> currentState
        }

        var repCompleted = false

        if (currentState == PushUpState.DOWN && newPushUpState == PushUpState.UP) {
            repCompleted = true
        }

        currentState = newPushUpState

        Log.d(TAG, "Angle = $elbowAngle")
        Log.d(TAG, "Stage = $newPushUpState")
        return PushUpDetectionResult(
            state = newPushUpState,
            elbowAngle = elbowAngle,
            repCompleted = repCompleted
        )
    }
}



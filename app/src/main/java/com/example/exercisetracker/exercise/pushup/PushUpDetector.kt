package com.example.exercisetracker.exercise.pushup

import android.util.Log
import com.example.exercisetracker.PoseLandmarkerHelper.LandmarkerListener
import com.example.exercisetracker.exercise.ExerciseDetector
import com.example.exercisetracker.exercise.DetectionDetails
import com.example.exercisetracker.exercise.DetectionResult
import com.example.exercisetracker.exercise.NoVisibleArm
import com.example.exercisetracker.pose.calculateElbowAngle
import com.example.exercisetracker.pose.getBestVisibleArm
import com.example.exercisetracker.pose.toArmJoints
import com.example.exercisetracker.ui.PoseDetectionListener
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

private const val TAG = "PoseDetector"
private const val UP_THRESHOLD = 160
private const val DOWN_THRESHOLD = 90

private const val VISIBILITY_THRESHOLD = 0.6

class PoseDetector : LandmarkerListener, ExerciseDetector {
    override var listener: PoseDetectionListener? = null
    private var currentPushUpState = PushUpState.UP
    override fun onError(error: String, errorCode: Int) {
        Log.d(TAG, "An error: $error with code $errorCode occurred")
    }

    override fun onResults(result: PoseLandmarkerResult) {
        if (result.landmarks().isEmpty()) return
        when (val outcome = process(result)) {
            is DetectionDetails -> listener?.onDetection(outcome)
            is NoVisibleArm -> {}
        }
    }

    override fun process(result: PoseLandmarkerResult): DetectionResult {
        val landmarks = result.landmarks().first()
        Log.i(TAG, landmarks.toString())
        val armJoints = landmarks.toArmJoints()
        val arm = getBestVisibleArm(armJoints) ?: return NoVisibleArm
        val elbowAngle = calculateElbowAngle(arm)

        val newPushUpState = when {
            elbowAngle > UP_THRESHOLD -> PushUpState.UP
            elbowAngle < DOWN_THRESHOLD -> PushUpState.DOWN
            else -> currentPushUpState
        }

        var repCompleted = false

        if (currentPushUpState == PushUpState.DOWN && newPushUpState == PushUpState.UP) {
            repCompleted = true
        }

        currentPushUpState = newPushUpState

        Log.d(TAG, "Angle = $elbowAngle")
        Log.d(TAG, "Stage = $newPushUpState")
        return DetectionDetails(
            pushUpState = newPushUpState,
            elbowAngle = elbowAngle,
            repCompleted = repCompleted
        )
    }
}



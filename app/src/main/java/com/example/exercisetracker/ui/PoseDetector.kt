package com.example.exercisetracker.ui

import android.util.Log
import com.example.exercisetracker.PoseLandmarkerHelper.LandmarkerListener
import com.example.exercisetracker.model.Arm
import com.example.exercisetracker.model.ArmsJoints
import com.example.exercisetracker.model.Stage
import com.example.exercisetracker.ui.extensions.toArmJoints
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.acos
import kotlin.math.sqrt

private const val TAG = "PoseDetector"
private const val UP_THRESHOLD = 160
private const val DOWN_THRESHOLD = 90

private const val VISIBILITY_THRESHOLD = 0.6

class PoseDetector : LandmarkerListener {
    var listener: PoseDetectionListener? = null
    private var currentStage = Stage.UP
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

    fun process(result: PoseLandmarkerResult): DetectionResult {
        val landmarks = result.landmarks().first()
        Log.i(TAG, landmarks.toString())
        val armJoints = landmarks.toArmJoints()
        val arm = getBestVisibleArm(armJoints) ?: return NoVisibleArm
        val elbowAngle = calculateElbowAngle(arm)

        val newStage = when {
            elbowAngle > UP_THRESHOLD -> Stage.UP
            elbowAngle < DOWN_THRESHOLD -> Stage.DOWN
            else -> currentStage
        }

        var repCompleted = false

        if (currentStage == Stage.DOWN && newStage == Stage.UP) {
            repCompleted = true
        }

        currentStage = newStage

        Log.d(TAG, "Angle = $elbowAngle")
        Log.d(TAG, "Stage = $newStage")
        return DetectionDetails(
            stage = newStage,
            elbowAngle = elbowAngle,
            repCompleted = repCompleted
        )
    }

    fun getBestVisibleArm(joints: ArmsJoints): Arm? {
        val leftVisibility = minOf(
            joints.leftShoulder.visibility().orElse(0f),
            joints.leftElbow.visibility().orElse(0f),
            joints.leftWrist.visibility().orElse(0f)
        )

        val rightVisibility = minOf(
            joints.rightShoulder.visibility().orElse(0f),
            joints.rightElbow.visibility().orElse(0f),
            joints.rightWrist.visibility().orElse(0f)
        )

        val bestVisibility = maxOf(leftVisibility, rightVisibility)

        if (bestVisibility < VISIBILITY_THRESHOLD) {
            Log.d(TAG, "Visibility ${bestVisibility * 100}%")
            return null
        }

        return if (leftVisibility >= rightVisibility) {
            Arm(
                shoulder = joints.leftShoulder,
                elbow = joints.leftElbow,
                wrist = joints.leftWrist
            )
        } else {
            Arm(
                shoulder = joints.rightShoulder,
                elbow = joints.rightElbow,
                wrist = joints.rightWrist
            )
        }
    }


    fun calculateElbowAngle(arm: Arm): Double {

        val shoulderX = arm.shoulder.x().toDouble()
        val shoulderY = arm.shoulder.y().toDouble()

        val elbowX = arm.elbow.x().toDouble()
        val elbowY = arm.elbow.y().toDouble()

        val wristX = arm.wrist.x().toDouble()
        val wristY = arm.wrist.y().toDouble()

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
}



package com.example.exercisetracker.ui

import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.exercisetracker.ExerciseTrackerApplication
import com.example.exercisetracker.PoseLandmarkerHelper
import com.example.exercisetracker.PoseLandmarkerHelper.LandmarkerListener
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.acos
import kotlin.math.sqrt


private const val TAG = "PoseViewModel"
private const val UP_THRESHOLD = 160
private const val DOWN_THRESHOLD = 90

class PoseViewModel(val poseLandmarkerHelper: PoseLandmarkerHelper,
    val poseDetector: PoseDetector) : ViewModel(), PoseDetectionListener {
    private val _uiState = MutableStateFlow(PoseUiState())
    val uiState = _uiState

    init {
        poseDetector.listener = this
        poseLandmarkerHelper.setupPoseLandmarker()
    }

    fun detectPoses(imageProxy: ImageProxy) {
        poseLandmarkerHelper.detectLiveStream(imageProxy, true)
    }

    fun toggleWorkout() {
        if (_uiState.value.isRecording) {
            stopWorkout()
        } else {
            startWorkout()
        }
    }

    fun startWorkout() {
        _uiState.update {
            it.copy(isRecording = true)
        }
    }

    fun stopWorkout() {
        _uiState.update {
            it.copy(isRecording = false)
        }
    }

    fun onRepDetected() {
        _uiState.update {
            it.copy(
                repCount = it.repCount + 1
            )
        }
    }

    private fun onPoseDetected(result: PoseLandmarkerResult) {
        Log.i(TAG, result.toString())
    }

    override fun onDetection(result: DetectionResult) {
        if (result.repCompleted) {
            onRepDetected()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ExerciseTrackerApplication)
                val poseLandmarkerHelper = application.container.poseLandmarkerHelper
                val poseDetector = application.container.poseDetector
                PoseViewModel(poseLandmarkerHelper = poseLandmarkerHelper,
                    poseDetector = poseDetector)
            }
        }
    }

}
interface PoseDetectionListener {
    fun onDetection(result: DetectionResult)
}

data class DetectionResult(
    val stage: Stage,
    val elbowAngle: Double,
    val repCompleted: Boolean
)

data class PoseUiState(
    val isRecording: Boolean = false,
    val repCount: Int = 0
)

class PoseDetector : LandmarkerListener {
    var listener: PoseDetectionListener? = null
    private var currentStage = Stage.UP
    override fun onError(error: String, errorCode: Int) {
        Log.d(TAG, "An error: $error with code $errorCode occurred")
    }

    override fun onResults(result: PoseLandmarkerResult) {
        if (result.landmarks().isEmpty()) return
        listener?.onDetection(process(result))
    }

    fun List<NormalizedLandmark>.toArmJoints(): ArmsJoints {
        return ArmsJoints(
            leftShoulder = this[11],
            leftElbow = this[13],
            leftWrist = this[15],
            rightShoulder = this[12],
            rightElbow = this[14],
            rightWrist = this[16]
        )
    }

    fun process(result: PoseLandmarkerResult): DetectionResult {
        val landmarks = result.landmarks().first()
        Log.i(TAG, landmarks.toString())
        val armJoints = landmarks.toArmJoints()
        val arm = getBestVisibleArm(armJoints)
        val elbowAngle = calculateElbowAngle(arm)

        val newStage = when {
            elbowAngle > UP_THRESHOLD -> Stage.UP
            elbowAngle < DOWN_THRESHOLD -> Stage.DOWN
            else -> Stage.UP
        }

        var repCompleted = false

        when (newStage) {
            Stage.DOWN -> {
                currentStage = Stage.DOWN
            }

            Stage.UP -> {
                if (currentStage == Stage.DOWN) {
                    repCompleted = true
                }
                currentStage = Stage.UP
            }
        }

        currentStage = newStage

        Log.d(TAG, "Angle = $elbowAngle")
        Log.d(TAG, "Stage = $newStage")
        return DetectionResult(
            stage = newStage,
            elbowAngle = elbowAngle,
            repCompleted = repCompleted
        )
    }

    fun getBestVisibleArm(joints: ArmsJoints): Arm {
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

data class ArmsJoints(
    val leftShoulder: NormalizedLandmark,
    val leftElbow: NormalizedLandmark,
    val leftWrist: NormalizedLandmark,
    val rightShoulder: NormalizedLandmark,
    val rightElbow: NormalizedLandmark,
    val rightWrist: NormalizedLandmark
)

data class Arm(
    val shoulder: NormalizedLandmark,
    val elbow: NormalizedLandmark,
    val wrist: NormalizedLandmark
)

enum class Stage {
    UP,
    DOWN
}
package com.example.exercisetracker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageProxy
import androidx.core.graphics.createBitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult


class PoseLandmarkerHelper(
    var minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
    var minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
    var minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
    var currentModel: Int = MODEL_POSE_LANDMARKER_LITE,
    var currentDelegate: Int = DELEGATE_CPU,
    var runningMode: RunningMode = RunningMode.LIVE_STREAM,
    val context: Context,
    // this listener is only used when running in RunningMode.LIVE_STREAM
    val poseLandmarkerHelperListener: LandmarkerListener? = null
) {
    private var poseLandmarker: PoseLandmarker? = null

    init {
        setupPoseLandmarker()
    }

    fun clearPoseLandmarker() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    // Return running status of PoseLandmarkerHelper
    fun isClose(): Boolean {
        return poseLandmarker == null
    }


    // Initialize the Pose landmarker using current settings on the
    // thread that is using it. CPU can be used with Landmarker
    // that are created on the main thread and used on a background thread, but
    // the GPU delegate needs to be used on the thread that initialized the
    // Landmarker
    fun setupPoseLandmarker() {
        val baseOptionsBuilder = BaseOptions.builder()
        val modelName = when (currentModel) {
            MODEL_POSE_LANDMARKER_FULL -> "pose_landmarker_full.task"
            MODEL_POSE_LANDMARKER_LITE -> "pose_landmarker_lite.task"
            MODEL_POSE_LANDMARKER_HEAVY -> "pose_landmarker_heavy.task"
            else -> "pose_landmarker_full.task"
        }

        baseOptionsBuilder.setModelAssetPath(modelName)

        when (runningMode) {
            RunningMode.LIVE_STREAM -> {
                if (poseLandmarkerHelperListener == null) {
                    throw IllegalStateException(
                        "poseLandmarkerHelperListener must be set when runningMode is LIVE_STREAM."
                    )
                }
            }

            else -> {
                // no-op
            }
        }
        try {

            val baseOptions = baseOptionsBuilder.build()

            val optionsBuilder =
                PoseLandmarker.PoseLandmarkerOptions.builder().setBaseOptions(baseOptions)
                    .setMinPoseDetectionConfidence(minPoseDetectionConfidence)
                    .setMinTrackingConfidence(minPoseTrackingConfidence)
                    .setMinPosePresenceConfidence(minPosePresenceConfidence)
                    .setResultListener(::returnLivestreamResult)
                    .setErrorListener(::returnLivestreamError)
                    .setRunningMode(runningMode)

            val options = optionsBuilder.build()
            poseLandmarker = PoseLandmarker.createFromOptions(context, options)

        } catch (e: IllegalStateException) {
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for " + "details"
            )
            Log.e(
                TAG, "MediaPipe failed to load the task with error: " + e.message
            )
        } catch (e: RuntimeException) {
            // This occurs if the model being used does not support GPU
            poseLandmarkerHelperListener?.onError(
                "Pose Landmarker failed to initialize. See error logs for " + "details", GPU_ERROR
            )
            Log.e(
                TAG, "Image classifier failed to load model with error: " + e.message
            )
        }
    }


    // Convert the ImageProxy to MP Image and feed it to PoselandmakerHelper.
    fun detectLiveStream(
        imageProxy: ImageProxy, isFrontCamera: Boolean
    ) {
        if (runningMode != RunningMode.LIVE_STREAM) {
            throw IllegalArgumentException(
                "Attempting to call detectLiveStream" + " while not using RunningMode.LIVE_STREAM"
            )
        }
        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer = createBitmap(imageProxy.width, imageProxy.height)

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // flip image if user use front camera
            if (isFrontCamera) {
                postScale(
                    -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
                )
            }
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height, matrix, true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        detectAsync(mpImage, frameTime)
    }

    // Run pose landmark using MediaPipe Pose Landmarker API
    @VisibleForTesting
    fun detectAsync(mpImage: MPImage, frameTime: Long) {
        poseLandmarker?.detectAsync(mpImage, frameTime)
        // As we're using running mode LIVE_STREAM, the landmark result will
        // be returned in returnLivestreamResult function
    }

    // Return the landmark result to this PoseLandmarkerHelper's caller
    private fun returnLivestreamResult(
        result: PoseLandmarkerResult, input: MPImage
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        poseLandmarkerHelperListener?.onResults(
            result
        )
    }

    // Return errors thrown during detection to this PoseLandmarkerHelper's
// caller
    private fun returnLivestreamError(error: RuntimeException) {
        poseLandmarkerHelperListener?.onError(
            error.message ?: "An unknown error has occurred"
        )
    }

    companion object {
        const val TAG = "PoseLandmarkerHelper"

        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5F
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5F
        const val DEFAULT_NUM_POSES = 1
        const val OTHER_ERROR = 0
        const val GPU_ERROR = 1
        const val MODEL_POSE_LANDMARKER_FULL = 0
        const val MODEL_POSE_LANDMARKER_LITE = 1
        const val MODEL_POSE_LANDMARKER_HEAVY = 2
    }

    interface LandmarkerListener {
        fun onError(error: String, errorCode: Int = OTHER_ERROR)
        fun onResults(result: PoseLandmarkerResult)
    }

}

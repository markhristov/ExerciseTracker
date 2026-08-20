package com.example.exercisetracker.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
class CameraManager(
    private val context: Context
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraExecutor: ExecutorService? = null
    private var startupGeneration = 0L

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onFrame: (ImageProxy) -> Unit = { imageProxy -> imageProxy.close() }
    ) {
        stopCamera()

        val generation = startupGeneration
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {

                val provider = try {
                    cameraProviderFuture.get()
                } catch (exception: Exception) {
                    Log.e(TAG, "Getting camera provider failed", exception)
                    return@addListener
                }

                cameraProvider = provider

                val resolutionSelector = ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
                    )
                    .build()

                val preview = Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()

                val executor = Executors.newSingleThreadExecutor()

                if (generation != startupGeneration) {
                    executor.shutdown()
                    return@addListener
                }

                cameraExecutor = executor
                imageAnalysis.setAnalyzer(executor, onFrame)

                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner = lifecycleOwner,
                        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (exception: Exception) {
                    Log.e(TAG, "Camera use case binding failed", exception)
                    stopCamera()
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun stopCamera() {
        startupGeneration++

        cameraProvider?.unbindAll()
        cameraProvider = null

        cameraExecutor?.shutdown()
        cameraExecutor = null
    }

    companion object {
        private const val TAG = "CameraManager"
    }
}

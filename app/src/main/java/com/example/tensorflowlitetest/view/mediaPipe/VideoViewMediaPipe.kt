package com.example.tensorflowlitetest.view.mediaPipe

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.tensorflowlitetest.components.TopAppBarCustom
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.CameraViewModelMediaPipe
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.util.concurrent.Executors

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoViewMediaPipe(
    vm: CameraViewModelMediaPipe, //View model injected from Hilt
    navController: NavController
) {
    //Log tag
    val TAG = "VideoView"

    //Context
    val context = LocalContext.current

    //AndroidView component
    val previewView: PreviewView = remember { PreviewView(context) }

    //Config state from view model
    val configState = vm.configState.collectAsState()
    val objectDetectorHelper = configState.value.objectDetectorHelper

    //Every time model id re-initialize object detector with new model
    LaunchedEffect(Unit) {
        vm.setRunningMode(RunningMode.LIVE_STREAM)
    }

    //Camera configuration
    val cameraController = remember { LifecycleCameraController(context) }

    //Init image analysis
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    //Using non blocking strategy: https://developer.android.com/media/camera/camerax/analyze
    cameraController.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST

    //Attach controller to preview component
    previewView.controller = cameraController

    //Executor to analyse video stream
    val executor = remember { Executors.newSingleThreadExecutor() }

    //Use image analyser for video stream
    cameraController.setImageAnalysisAnalyzer(executor) { image: ImageProxy ->

        //Image rotation, and save value in state
        val imageRotation = image.imageInfo.rotationDegrees

        //Image like bitmap
        val bitmapImg = image.toBitmap()

        fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }

        //Detection of image
        val rotateImage = bitmapImg.rotate(imageRotation.toFloat())

        //Detection of image
        //objectDetectorHelper?.detectImage(rotateImage)
        //Log.d(TAG, "Image info: " + image.)
        vm.setBitmapImage(bitmapImg)
        if(vm.getRunningMode() === RunningMode.LIVE_STREAM) {
            objectDetectorHelper?.detectLivestreamFrame(image)
        }
        // after done, release the ImageProxy object
        image.close()
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(navController = navController)
        }
    ) {
        it ->
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f)
            )
            ObjectDetectionViewMediaPipe(
                navController = navController,
                vm = vm,
                scaleFactor = 2.2f,
                usedFromVideoView = true
            )
        }
    }
}
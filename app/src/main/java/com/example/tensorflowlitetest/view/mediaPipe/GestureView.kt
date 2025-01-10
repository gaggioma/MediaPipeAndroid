package com.example.tensorflowlitetest.view.mediaPipe

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.tensorflowlitetest.components.TopAppBarCustom
import com.example.tensorflowlitetest.view.KeepScreenOn
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.GestureLeftViewModel
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.GestureRightViewModel
import java.util.concurrent.Executors

@Composable
fun GestureView(
    navController: NavController
){

    //Log tag
    val TAG = "GestureViewModel"

    //Keep screen on during app run
    KeepScreenOn()

    //View model
    val vmLeft: GestureLeftViewModel = hiltViewModel()
    val vmRight: GestureRightViewModel = hiltViewModel()

    //Config state left
    val configStateLeft = vmLeft.configState.collectAsState()
    val gestureHelperLeft = configStateLeft.value.config

    //Config state right
    val configStateRight = vmRight.configState.collectAsState()
    val gestureHelperRight = configStateRight.value.config

    //Context
    val context = LocalContext.current

    //AndroidView component
    val previewView: PreviewView = remember { PreviewView(context) }

    //Set all needed for camera behaviour
    //Camera configuration
    val cameraController = remember { LifecycleCameraController(context) }

    //Init image analysis
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    //Using non blocking strategy: https://developer.android.com/media/camera/camerax/analyze
    cameraController.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST

    //Attach controller to preview component
    previewView.controller = cameraController

    //Executor to analyse video stream
    val executor = remember { Executors.newSingleThreadExecutor() }

    //Analyze image gesture from camera image proxy
    cameraController.setImageAnalysisAnalyzer(executor){
        image: ImageProxy ->

        //Convert ImageProxy into bitmap
        val imageBitmap : Bitmap = image.toBitmap()

        //Left image
        val leftBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width/2, imageBitmap.height)

        //Right image
        val rightBitmap = Bitmap.createBitmap(imageBitmap, imageBitmap.width/2, 0, imageBitmap.width/2, imageBitmap.height)

        //Save image into model view
        //vm.setBitmapImage(imageBitmap)

        //Gesture detection
        if(gestureHelperLeft !== null && gestureHelperRight != null) {
            //gestureHelperLeft.recognizeLiveStream(image)
            gestureHelperLeft.recognizeImage(leftBitmap, image.imageInfo.rotationDegrees.toFloat())
            gestureHelperRight.recognizeImage(rightBitmap, image.imageInfo.rotationDegrees.toFloat())
        }
        // after done, release the ImageProxy object
        image.close()
    }

    //View components
    Scaffold(
        topBar = {
            TopAppBarCustom(navController = navController)
        }

    ) { it ->
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

            GestureDetectionView()
        }
    }
}
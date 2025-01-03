package com.example.tensorflowlitetest.view.tfLite

import android.util.Log
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.tensorflowlitetest.components.TopAppBarCustom
import com.example.tensorflowlitetest.view.tfLite.viewModel.CameraViewModel
import java.util.concurrent.Executors

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoView(
    vm: CameraViewModel, //View model injected from Hilt
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
    val tfIsReady = configState.value.initilized
    val objectDetectorHelper = configState.value.objectDetectorHelper
    val modelId = configState.value.modelId
    val scoreThreshold = configState.value.scoreThreshold
    val numThreads = configState.value.numThreads
    val maxResults = configState.value.maxResults

    //List of models state
    val openModelList = rememberSaveable { mutableStateOf(false) }

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
        //photoRotation.intValue = imageRotation
        Log.d(TAG, "Image rotation $imageRotation")

        //Image like bitmap
        val bitmapImg = image.toBitmap()

        //Detection of image
        objectDetectorHelper!!.detect(bitmapImg, imageRotation)

        // after done, release the ImageProxy object
        image.close()
    }

    //Every time model id re-initialize object detector with new model
    LaunchedEffect(modelId, scoreThreshold, numThreads, maxResults) {
        //Re-initialize object detector with new model
        vm.initObjectDetectorHelper(modelId, scoreThreshold, numThreads, maxResults)
    }

    //detection model change handler
    fun detectionModelChangeHandler(value:String){
        //Update state
        vm.setModelId(Integer.parseInt(value))
    }

    fun toggleModelListHandler(){
        openModelList.value = !openModelList.value
    }

    Scaffold(
        topBar = {
                 TopAppBarCustom(navController = navController)
        },
        bottomBar = {
            //BottomAppBarCustom(navController)
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
            ObjectDetectionView(
                navController = navController,
                vm = vm,
                scaleFactor = 2.2f,
                usedFromVideoView = true
            )
        }
    }
}
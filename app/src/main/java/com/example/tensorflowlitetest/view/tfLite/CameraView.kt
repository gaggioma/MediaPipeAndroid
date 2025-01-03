package com.example.tensorflowlitetest.view.tfLite

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tensorflowlitetest.components.TopAppBarCustom
import com.example.tensorflowlitetest.view.tfLite.viewModel.CameraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraView(
    vm: CameraViewModel, //View model injected from Hilt
    navController: NavController
) {
    //Log tag
    val TAG = "CameraView"

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

    //Camera configuration
    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    //List of models state
    val openModelList = rememberSaveable { mutableStateOf(false) }
    fun toggleModelListHandler(){
        openModelList.value = !openModelList.value
    }

    //detection model change handler
    fun detectionModelChangeHandler(value:String){
        //Update state
        vm.setModelId(Integer.parseInt(value))
    }

    //Every time model id re-initialize object detector with new model
    LaunchedEffect(modelId, scoreThreshold, numThreads, maxResults) {
        //Re-initialize object detector with new model
        vm.initObjectDetectorHelper(modelId, scoreThreshold, numThreads, maxResults)
    }

    //Handle take a picture click
    fun takeAPicture(){
        vm.setBitmapImage(null)

        cameraController.takePicture(ContextCompat.getMainExecutor(context),

            object: ImageCapture.OnImageCapturedCallback(){

                override fun onCaptureSuccess(image: ImageProxy) {

                    //Image rotation, and save value in state
                    val imageRotation = image.imageInfo.rotationDegrees
                    //photoRotation.intValue = imageRotation
                    Log.d(TAG, "Image rotation $imageRotation")

                    //Image like bitmap
                    val bitmapImg = image.toBitmap()

                    //Save into vm
                    //vm.setBitmapImage(bitmapImg)
                    //photoState.value = bitmapImg

                    //Loading
                    vm.setLoading(true)

                    //Detection of image
                    objectDetectorHelper!!.detect(bitmapImg, imageRotation)

                    navController.navigate(
                        route = "objectDetectionView"
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
        })

        /*cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    //Update state
                    photoState.value = savedUri.toString()

                    val bitmapImg: Bitmap = BitmapFactory.decodeStream(photoFile.inputStream())
                    objectDetectorHelper!!.detect(bitmapImg, 0)
                }
            })*/
    }

    Scaffold(
        topBar = {
            TopAppBarCustom(navController = navController)
        },
        bottomBar = {
        },
        floatingActionButton = {
            if(!openModelList.value){
                FloatingActionButton(
                    onClick = {takeAPicture()}) {
                    Icon(Icons.Default.Check, contentDescription = "Make photo")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        it ->
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        }
    }
}
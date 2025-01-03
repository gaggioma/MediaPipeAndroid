package com.example.tensorflowlitetest.view.tfLite.viewModel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.tensorflowlitetest.utils.tfLite.ObjectDetectorHelper
import com.example.tensorflowlitetest.view.tfLite.viewModel.models.CameraViewResult
import com.example.tensorflowlitetest.view.tfLite.viewModel.models.DetectorConfigModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.tensorflow.lite.task.gms.vision.detector.Detection
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext val context: Context
): ViewModel(), ObjectDetectorHelper.DetectorListener {

    //Result state
    private val _uiState = MutableStateFlow(CameraViewResult())
    val uiState: StateFlow<CameraViewResult> = _uiState

    //Configuration state
    private val _configState = MutableStateFlow(DetectorConfigModel())
    val configState: StateFlow<DetectorConfigModel> = _configState

    //Config handlers
    fun initObjectDetectorHelper(
        modelId:Int,
        scoreThreshold: Float,
        numThreads: Int,
        maxResults: Int
        ){
        val newState: DetectorConfigModel = _configState.value.copy()
        //Init detector
        newState.objectDetectorHelper = ObjectDetectorHelper(
            context = context,
            objectDetectorListener = this,
            currentModel = modelId,
            threshold = scoreThreshold,
            numThreads = numThreads,
            maxResults = maxResults
        )
        _configState.value = newState
    }

    fun setModelId(id: Int){
        val newState: DetectorConfigModel = _configState.value.copy()
        newState.modelId = id
        _configState.value = newState
    }


    //Result handlers
    fun setLoading(loading: Boolean){
        val newState: CameraViewResult = _uiState.value.copy()
        newState.loading = loading
        _uiState.value = newState
    }

    fun setBitmapImage(img: Bitmap?){
        val newState: CameraViewResult = _uiState.value.copy()
        newState.photoImage = img
        _uiState.value = newState
    }

    //Superclass implementation
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int,
        photoImage: Bitmap,
        photoRotation: Int
    ) {
        //Log.d("CameraViewModel", "processed in ${inferenceTime}ms" )
        val newState: CameraViewResult = _uiState.value.copy()
        if (results != null) {
            newState.results = results
        }
        newState.inferenceTime = inferenceTime
        newState.imageHeight = imageHeight
        newState.imageWidth = imageWidth
        newState.loading = false
        newState.photoImage = photoImage
        newState.photoRotation = photoRotation
        _uiState.value = newState
    }

    override fun onError(error: String) {
        val newState: CameraViewResult = _uiState.value.copy()
        newState.error = error
        newState.loading = false
        _uiState.value = newState
    }

    override fun onInitialized() {
        val newState: DetectorConfigModel = _configState.value.copy()
        newState.initilized = true
        _configState.value = newState
    }
}
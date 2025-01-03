package com.example.tensorflowlitetest.view.mediaPipe.viewModel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.tensorflowlitetest.utils.mediaPipe.ObjectDetectorHelper
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.models.CameraViewResultMediaPipe
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.models.DetectorConfigModelMediaPipe
import com.google.mediapipe.tasks.vision.core.RunningMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CameraViewModelMediaPipe @Inject constructor(
    @ApplicationContext val context: Context
): ViewModel(), ObjectDetectorHelper.DetectorListener {

    //Result state
    private val _uiState = MutableStateFlow(CameraViewResultMediaPipe())
    val uiState: StateFlow<CameraViewResultMediaPipe> = _uiState

    //Configuration state
    private val _configState = MutableStateFlow(DetectorConfigModelMediaPipe())
    val configState: StateFlow<DetectorConfigModelMediaPipe> = _configState

    init {
        initObjectDetectorHelper()
    }

    //Config handlers
    private fun initObjectDetectorHelper(){

        //val newState: DetectorConfigModelMediaPipe = _configState.value.copy()
        //Init detector
        /*newState.objectDetectorHelper = ObjectDetectorHelperMediaPipe(
            context = context,
            objectDetectorListener = this,
            currentModel = _configState.value.modelId,
            threshold = _configState.value.scoreThreshold,
            maxResults = _configState.value.maxResults,
            runningMode = _configState.value.runningMode
        )*/
        _configState.value.objectDetectorHelper = ObjectDetectorHelper(
            context = context,
            objectDetectorListener = this,
            currentModel = _configState.value.modelId,
            threshold = _configState.value.scoreThreshold,
            maxResults = _configState.value.maxResults
        )
    }

    fun setRunningMode(value : RunningMode){
        val newState: DetectorConfigModelMediaPipe = _configState.value.copy()
        newState.runningMode = value
        newState.objectDetectorHelper!!.runningMode = value
        _configState.value = newState
    }

    fun getRunningMode(): RunningMode{
        return _configState.value.objectDetectorHelper!!.runningMode
    }


    fun setModelId(value: Int){
        val newState: DetectorConfigModelMediaPipe = _configState.value.copy()
        newState.modelId = value
        newState.objectDetectorHelper!!.currentModel = value
        newState.objectDetectorHelper!!.setupObjectDetector()
        _configState.value = newState

        //_configState.value.objectDetectorHelper!!.clearObjectDetector()
        //_configState.value.objectDetectorHelper!!.setupObjectDetector()
    }

    fun getModelId(): Int{
        return _configState.value.modelId
    }

    fun setScoreThreshold(value: Float){
        val newState: DetectorConfigModelMediaPipe = _configState.value.copy()
        newState.scoreThreshold = value
        newState.objectDetectorHelper!!.threshold = value
        newState.objectDetectorHelper!!.setupObjectDetector()
        _configState.value = newState
    }

    fun getScoreThreshold(): Float{
        return _configState.value.scoreThreshold
    }

    fun setMaxResults(value: Int){
        val newState: DetectorConfigModelMediaPipe = _configState.value.copy()
        newState.maxResults = value
        newState.objectDetectorHelper!!.maxResults = value
        newState.objectDetectorHelper!!.setupObjectDetector()
        _configState.value = newState
    }

    fun getMaxResults(): Int{
        return _configState.value.maxResults
    }

    //Result handlers
    fun setLoading(loading: Boolean){
        val newState: CameraViewResultMediaPipe = _uiState.value.copy()
        newState.loading = loading
        _uiState.value = newState
    }

    fun setBitmapImage(img: Bitmap?){
        val newState: CameraViewResultMediaPipe = _uiState.value.copy()
        newState.photoImage = img
        _uiState.value = newState
    }

    //Superclass implementation
    override fun onResults(
        resultBundle: ObjectDetectorHelper.ResultBundle
        /*results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int,
        photoImage: Bitmap,
        photoRotation: Int*/
    ) {
        //Log.d("CameraViewModel", "processed in ${inferenceTime}ms" )

        val newState: CameraViewResultMediaPipe = _uiState.value.copy()
        newState.results = resultBundle.results.toMutableList()
        newState.inferenceTime = resultBundle.inferenceTime
        newState.imageHeight = resultBundle.inputImageHeight
        newState.imageWidth = resultBundle.inputImageWidth
        newState.loading = false
        newState.photoRotation = resultBundle.inputImageRotation
        _uiState.value = newState


    }

    override fun onError(error: String, errorCode: Int) {
        val newState: CameraViewResultMediaPipe = _uiState.value.copy()
        newState.error = error
        newState.loading = false
        _uiState.value = newState
    }
}
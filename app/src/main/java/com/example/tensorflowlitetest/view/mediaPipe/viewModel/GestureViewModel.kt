package com.example.tensorflowlitetest.view.mediaPipe.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tensorflowlitetest.utils.mediaPipe.GestureRecognizerHelper
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.models.GestureConfig
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.models.GestureResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class GestureViewModel @Inject constructor(
    @ApplicationContext val context: Context
): ViewModel(), GestureRecognizerHelper.GestureRecognizerListener {

    val TAG = "GestureViewModel"

    //Result state
    private val _uiState = MutableStateFlow(GestureResult())
    val uiState: StateFlow<GestureResult> = _uiState

    //Config state
    private val _configState = MutableStateFlow(GestureConfig())
    val configState: StateFlow<GestureConfig> = _configState

    //Init gesture on model view creation
    init {
        setSeed()
        initGesture()
    }

    private fun initGesture(){
        var copyState = _configState.value.copy()
        copyState.config = GestureRecognizerHelper(
            context = context,
            gestureRecognizerListener = this,
            runningMode = copyState.runningMode
        )
        _configState.value = copyState
    }

    fun setSeed(){
        //var copyState = _uiState.value.copy()
        val rand = Math.random()
        Log.d(TAG, "Model view id: $rand")
    }

    fun setBitmapImage(image: Bitmap){
        val copyState = _uiState.value.copy()
        copyState.inputImage = image
        _uiState.value = copyState
    }

    fun setImageRotation(degrees: Int){
        var copyState = _uiState.value.copy()
        copyState.imageRotation = degrees
        _uiState.value = copyState
    }

    fun updateFrameNumber(){
        var copyState = _uiState.value.copy()
        var newFrameNumber = copyState.frameNumber + 1
        copyState.frameNumber++
        //Log.d(TAG, "new frame number: $newFrameNumber")
        _uiState.value = copyState
    }

    override fun onError(error: String, errorCode: Int) {
        var copyState = _uiState.value.copy()
        copyState.error = error
        copyState.errorCode = errorCode
        _uiState.value = copyState
    }

    override fun onResults(
        resultBundle: GestureRecognizerHelper.ResultBundle,
        inputImage: Bitmap
    ) {
        //Log.d(TAG, "$resultBundle")
        val copyState = _uiState.value.copy()
        val results = resultBundle.results.toMutableList()
        copyState.results = results
        copyState.inferenceTime = resultBundle.inferenceTime
        copyState.inputImageHeight = resultBundle.inputImageHeight
        copyState.inputImageWidth = resultBundle.inputImageWidth
        copyState.inputImage = inputImage
        _uiState.value = copyState
    }
}
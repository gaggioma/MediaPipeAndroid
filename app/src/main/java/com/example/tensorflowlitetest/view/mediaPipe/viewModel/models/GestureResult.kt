package com.example.tensorflowlitetest.view.mediaPipe.viewModel.models

import android.graphics.Bitmap
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

data class GestureResult(
    var inputImage: Bitmap? = null,
    var imageRotation: Int = 0,
    var results: MutableList<GestureRecognizerResult> = ArrayList(),
    var inferenceTime: Long = 0,
    var inputImageHeight: Int = 0,
    var inputImageWidth: Int = 0,
    var error: String = "",
    var errorCode: Int = 0,
    var frameNumber: Int = 0,
)

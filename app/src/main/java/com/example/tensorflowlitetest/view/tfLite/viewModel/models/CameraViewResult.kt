package com.example.tensorflowlitetest.view.tfLite.viewModel.models

import android.graphics.Bitmap
import org.tensorflow.lite.task.gms.vision.detector.Detection

data class CameraViewResult(
    var photoImage: Bitmap? = null,
    var photoRotation: Int = 0,
    var results: MutableList<Detection> = ArrayList(),
    var inferenceTime: Long = 0,
    var imageHeight: Int = 0,
    var imageWidth: Int = 0,
    var error: String = "",
    var loading: Boolean = false
)
package com.example.tensorflowlitetest.view.mediaPipe.viewModel.models

import android.graphics.Bitmap
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

data class CameraViewResultMediaPipe(
    var photoImage: Bitmap? = null,
    var photoRotation: Int = 0,
    var results: MutableList<ObjectDetectorResult> = ArrayList(),
    var inferenceTime: Long = 0,
    var imageHeight: Int = 0,
    var imageWidth: Int = 0,
    var error: String = "",
    var loading: Boolean = false
)
package com.example.tensorflowlitetest.view.tfLite.viewModel.models

import com.example.tensorflowlitetest.utils.tfLite.ObjectDetectorHelper

data class DetectorConfigModel(
    var initilized: Boolean = false,
    var objectDetectorHelper: ObjectDetectorHelper? = null,
    var modelId: Int = 0,
    var scoreThreshold: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 3
)
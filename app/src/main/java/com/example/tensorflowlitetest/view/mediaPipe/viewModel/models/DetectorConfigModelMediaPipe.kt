package com.example.tensorflowlitetest.view.mediaPipe.viewModel.models

import com.example.tensorflowlitetest.utils.mediaPipe.ObjectDetectorHelper
import com.google.mediapipe.tasks.vision.core.RunningMode

data class DetectorConfigModelMediaPipe(
    var initilized: Boolean = false,
    var objectDetectorHelper: ObjectDetectorHelper? = null,
    var modelId: Int = 1,
    var scoreThreshold: Float = 0.3f,
    var maxResults: Int = 5,
    var runningMode: RunningMode = RunningMode.LIVE_STREAM
)
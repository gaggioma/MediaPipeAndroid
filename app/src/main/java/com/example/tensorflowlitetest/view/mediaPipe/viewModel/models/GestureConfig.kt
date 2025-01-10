package com.example.tensorflowlitetest.view.mediaPipe.viewModel.models

import com.example.tensorflowlitetest.utils.mediaPipe.GestureRecognizerHelper
import com.google.mediapipe.tasks.vision.core.RunningMode

data class GestureConfig(
    var config: GestureRecognizerHelper? = null,
    var runningMode: RunningMode =RunningMode.IMAGE, //RunningMode.LIVE_STREAM,
)

package com.example.tensorflowlitetest.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tensorflowlitetest.view.mediaPipe.GestureView
import com.example.tensorflowlitetest.view.mediaPipe.ObjectDetectionViewMediaPipe
import com.example.tensorflowlitetest.view.mediaPipe.VideoViewMediaPipe
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.CameraViewModelMediaPipe

@Composable
fun NavigationView() {

    //Define navigation controller
    val navController = rememberNavController()

    //Unique view model share between view
    val vmmediapipe: CameraViewModelMediaPipe = hiltViewModel()

    //Nav host
    NavHost(navController = navController, startDestination = "gestureView") {

        composable("objectDetectionViewMediaPipe") {
            ObjectDetectionViewMediaPipe(navController = navController, vm = vmmediapipe)
        }

        composable("videoViewMediaPipe") {
            VideoViewMediaPipe(navController = navController, vm = vmmediapipe)
        }

        composable("gestureView"){
            GestureView(navController = navController)
        }
    }
}
package com.example.tensorflowlitetest.view.mediaPipe

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.GestureViewModel
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GestureDetectionView(
    scaleFactor:Float =2f,
){

    val TAG = "GestureDetectionView"

    val vm :GestureViewModel = hiltViewModel()

    //View model
    val uiState = vm.uiState.collectAsState()
    val image = uiState.value.inputImage
    val results = uiState.value.results

    //Fun display result
    @Composable
    fun displayScore(){
        var maxCategory: String? = "";
        if(results.size > 0){
            val result: GestureRecognizerResult = results.get(0)
            var sortedCategories = listOf<Category>()
            for(gesture in result.gestures()){
                sortedCategories = gesture.sortedByDescending { it.score() }

            }
            if(sortedCategories.size != 0) {
                maxCategory = sortedCategories.get(0).categoryName()
            }
        }
        val iconScale = 100.sp
        when(maxCategory){
            "Closed_Fist" -> Text(fontSize = iconScale, text = "✊")
            "Open_Palm" -> Text(fontSize = iconScale, text = "🖐️")
            "Pointing_Up" -> Text(fontSize = iconScale, text = "☝️")
            "Thumb_Down" -> Text(fontSize = iconScale, text = "👎")
            "Thumb_Up" -> Text(fontSize = iconScale, text = "👍")
            "Victory" -> Text(fontSize = iconScale, text = "✌️")
            "ILoveYou" -> Text(fontSize = iconScale, text = "🤟")
            else -> Text(fontSize = iconScale, text = "🤷‍♂️")
        }
    }


    if(image != null){

        Scaffold{

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    displayScore()
                }

                Canvas(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    onDraw = {

                        //Scale bitmap
                        val scaledPhoto = Bitmap.createScaledBitmap(
                            image,
                            Math.round(image.width * scaleFactor),
                            Math.round(image.height * scaleFactor),
                            false
                        )

                        //Image suitable for drawind into drawImage
                        val photoAsImageBitmap = scaledPhoto.asImageBitmap()

                        //Translation
                        val left: Float = 50f
                        val top: Float = 50f

                        translate(left = left, top = top) {

                            //Image
                            drawImage(
                                image = photoAsImageBitmap,
                            )

                            //Landmark
                            val width = photoAsImageBitmap.width
                            val height = photoAsImageBitmap.height
                            if (results.size > 0) {
                                val result: GestureRecognizerResult = results.get(0)
                                if (result.landmarks().size != 0) {
                                    Log.d(
                                        TAG,
                                        "ladmarks size: ${result.landmarks().get(0).size}"
                                    )

                                    //First landmark how starting point. Scale every landmark into picture
                                    val firstLandmark = result.landmarks().get(0).get(0)
                                    val path = Path()
                                    path.moveTo(
                                        firstLandmark.x() * width,
                                        firstLandmark.y() * height
                                    )
                                    //circle
                                    drawCircle(color = Color.Cyan, radius = 10f, center = Offset(firstLandmark.x() * width, firstLandmark.y() * height))

                                    //others landmarks.
                                    for (i in 1 until result.landmarks().get(0).size) {
                                        val landmark = result.landmarks()[0][i]
                                        path.lineTo(
                                            landmark.x() * width,
                                            landmark.y() * height
                                        )
                                        //circle
                                        drawCircle(color = Color.Cyan, radius = 10f, center = Offset(landmark.x() * width, landmark.y() * height))
                                    }
                                    drawPath(
                                        path,
                                        color = Color.LightGray,
                                        style = Stroke(width = 8f, join = StrokeJoin.Round)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }



}
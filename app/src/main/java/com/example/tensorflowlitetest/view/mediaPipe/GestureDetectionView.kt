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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.GestureLeftViewModel
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.GestureRightViewModel
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GestureDetectionView(
    scaleFactor:Float =2f,
){

    val TAG = "GestureDetectionView"

    val vmLeft : GestureLeftViewModel = hiltViewModel()
    val vmRight : GestureRightViewModel = hiltViewModel()

    //View model Left
    val uiStateLeft = vmLeft.uiState.collectAsState()
    val imageLeft = uiStateLeft.value.inputImage
    val resultsLeft = uiStateLeft.value.results

    //View model Right
    val uiStateRight = vmRight.uiState.collectAsState()
    val imageRight = uiStateRight.value.inputImage
    val resultsRight = uiStateRight.value.results

    //Screen
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    fun getMaxCategory(results: List<GestureRecognizerResult>) : String?{
        var maxCategory: String? = "";
        if(results.isNotEmpty()){
            val result: GestureRecognizerResult = results.get(0)
            var sortedCategories = listOf<Category>()
            for(gesture in result.gestures()){
                sortedCategories = gesture.sortedByDescending { it.score() }

            }
            if(sortedCategories.isNotEmpty()) {
                maxCategory = sortedCategories.get(0).categoryName()
            }
        }
        return maxCategory
    }

    //Fun display result
    @Composable
    fun displayScore(results: List<GestureRecognizerResult>){
        var maxCategory: String? = getMaxCategory(results)
        val iconScale = 50.sp
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

    @Composable
    fun victoryIcon(){
        val iconScale = 50.sp
        Text(fontSize = iconScale, text = "🥇")
    }

    //Fun display result
    @Composable
    fun displayWinner(resultLeft:String, resultRight: String): String{

        if(resultLeft == "Closed_Fist" && resultRight == "Victory"){
            return "left"
        }

        if(resultLeft == "Open_Palm" && resultRight == "Closed_Fist"){
            return "left"
        }

        if(resultLeft == "Victory" && resultRight == "Open_Palm"){
            return "left"
        }

        if(resultLeft == "Closed_Fist" && resultRight == "Open_Palm"){
            return "right"
        }

        if(resultLeft == "Open_Palm" && resultRight == "Victory"){
            return "right"
        }

        if(resultLeft == "Victory" && resultRight == "Closed_Fist"){
            return "right"
        }

        return "none"
    }


    if(imageLeft != null && imageRight != null){

        Scaffold{

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    val maxCategoryLeft = getMaxCategory(resultsLeft)
                    val maxCategoryRight = getMaxCategory(resultsRight)
                    Row {
                        displayScore(resultsLeft)
                        if(displayWinner(maxCategoryLeft!!, maxCategoryRight!!) == "left"){
                            victoryIcon()
                        }
                    }

                    Row {
                        if(displayWinner(maxCategoryLeft!!, maxCategoryRight!!) == "right"){
                            victoryIcon()
                        }
                        displayScore(resultsRight)
                    }
                }

                Row(
                    modifier = Modifier.width(screenWidth - imageRight.width.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    //Left image
                    Canvas(
                    modifier = Modifier.padding(start = 70.dp)
                        ,
                    onDraw = {

                        //Scale bitmap
                        val scaledPhoto = Bitmap.createScaledBitmap(
                            imageLeft,
                            Math.round(imageLeft.width * scaleFactor),
                            Math.round(imageLeft.height * scaleFactor),
                            false
                        )

                        //Image suitable to draw into drawImage
                        val photoAsImageBitmap = scaledPhoto.asImageBitmap()

                        //Translation
                        val left: Float = 0f
                        val top: Float = 0f

                        translate(left = left, top = top) {

                            //Image
                            drawImage(
                                image = photoAsImageBitmap,
                            )

                            //Landmark
                            val width = photoAsImageBitmap.width
                            val height = photoAsImageBitmap.height
                            if (resultsLeft.size > 0) {
                                val result: GestureRecognizerResult = resultsLeft.get(0)
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
                //End left image

                    Canvas(
                        modifier = Modifier
                            //.padding(it)
                            ,
                        onDraw = {

                            //Scale bitmap
                            val scaledPhoto = Bitmap.createScaledBitmap(
                                imageRight,
                                Math.round(imageRight.width * scaleFactor),
                                Math.round(imageRight.height * scaleFactor),
                                false
                            )

                            //Image suitable to draw into drawImage
                            val photoAsImageBitmap = scaledPhoto.asImageBitmap()

                            //Translation
                            val left: Float = 0f
                            val top: Float = 0f

                            translate(left = left, top = top) {

                                //Image
                                drawImage(
                                    image = photoAsImageBitmap,
                                )

                                //Landmark
                                val width = photoAsImageBitmap.width
                                val height = photoAsImageBitmap.height
                                if (resultsRight.size > 0) {
                                    val result: GestureRecognizerResult = resultsRight.get(0)
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
                                        drawCircle(
                                            color = Color.Cyan,
                                            radius = 10f,
                                            center = Offset(
                                                firstLandmark.x() * width,
                                                firstLandmark.y() * height
                                            )
                                        )

                                        //others landmarks.
                                        for (i in 1 until result.landmarks().get(0).size) {
                                            val landmark = result.landmarks()[0][i]
                                            path.lineTo(
                                                landmark.x() * width,
                                                landmark.y() * height
                                            )
                                            //circle
                                            drawCircle(
                                                color = Color.Cyan,
                                                radius = 10f,
                                                center = Offset(
                                                    landmark.x() * width,
                                                    landmark.y() * height
                                                )
                                            )
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
                    )//End right image
                }
            }
        }
    }



}
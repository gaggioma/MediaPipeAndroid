package com.example.tensorflowlitetest.view.tfLite

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tensorflowlitetest.R
import com.example.tensorflowlitetest.view.tfLite.viewModel.CameraViewModel
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectDetectionView(
    navController: NavController,
    vm: CameraViewModel, //View model injected from Hilt
    scaleFactor:Float = 0.4f,
    usedFromVideoView:Boolean = false
) {
    val TAG = "ObjectDetectionView"

    //Result state from view model
    val uiState = vm.uiState.collectAsState()
    val photoWidth = uiState.value.imageWidth
    val photoHeight = uiState.value.imageHeight
    val bitmapPhoto = uiState.value.photoImage
    val detectedObjectArray = uiState.value.results
    val photoRotation = uiState.value.photoRotation

    val textMeasurer = rememberTextMeasurer()

    Scaffold(
        topBar = {
            if(!usedFromVideoView) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.tensorflow_icon),
                                contentDescription = "tensor_flow_icon"
                            )
                            Image(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.object_detection_icon),
                                contentDescription = "object_detection_icon"
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                vm.setBitmapImage(null)
                                navController.popBackStack()
                            }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if(!usedFromVideoView) {
                //BottomAppBarCustom(navController)
            }
        }

    ) {

            if (bitmapPhoto !== null) {
                Canvas(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    onDraw = {

                        //Scale bitmap
                        val scaledPhoto = Bitmap.createScaledBitmap(
                            bitmapPhoto,
                            Math.round(bitmapPhoto.width * scaleFactor),
                            Math.round(bitmapPhoto.height * scaleFactor),
                            false
                        )

                        //Image suitable for drawind into drawImage
                        val photoAsImageBitmap = scaledPhoto.asImageBitmap()

                        //Translation
                        var left: Float = 0f
                        var top: Float = 0f
                        if(photoRotation != 0){
                            left = -500f
                            top = -100f
                        }

                        translate(left = left, top = top) {
                            rotate(
                                degrees = photoRotation.toFloat(),
                            ) {

                                //Image
                                drawImage(
                                    image = photoAsImageBitmap,
                                    //dstSize = IntSize(canvasWidth, canvasHeight)
                                )

                                //Detected rectangles
                                for (resultIdx in detectedObjectArray.indices) {

                                    val category = detectedObjectArray[resultIdx].categories[0].label
                                    val score = round(detectedObjectArray[resultIdx].categories[0].score*100)/100
                                    val boundingBox = detectedObjectArray[resultIdx].boundingBox
                                    val top = boundingBox.top * scaleFactor
                                    val bottom = boundingBox.bottom * scaleFactor
                                    val left = boundingBox.left * scaleFactor
                                    val right = boundingBox.right * scaleFactor

                                    val categoryText = "$category, score: $score"
                                    Log.d(TAG, "object $categoryText")


                                    //Create a rect around detected object
                                    val path = Path()
                                    path.moveTo(left, top)
                                    path.lineTo(right, top)
                                    path.lineTo(right, bottom)
                                    path.lineTo(left, bottom)
                                    path.close()
                                    drawPath(path, color = Color.Red, style = Stroke(width = 10f))

                                    //Draw category and score into text. To avoid error "maxWidth must be >= than minWidth(0) android"
                                    val textResult = textMeasurer.measure(
                                        AnnotatedString(
                                            categoryText, spanStyle = SpanStyle(
                                                fontSize = 10.sp,
                                                color = Color.Green,
                                                background = Color.Red.copy(alpha = 0.1f)
                                            )
                                        )
                                    )
                                    /*drawText(
                                        textMeasurer = textMeasurer,
                                        text = categoryText,
                                        style = TextStyle(
                                            fontSize = 10.sp,
                                            color = Color.Green,
                                            background = Color.Red.copy(alpha = 0.1f)
                                        ),
                                        topLeft = Offset(
                                            x = left,
                                            y = top
                                        )
                                    )*/
                                    drawText(textResult,
                                        topLeft = Offset(
                                            x = left,
                                            y = top
                                        )
                                    )
                                }
                            }
                        }

                    }
                )
            } else {
                Text(text = "No image avilable", modifier = Modifier)
            }

    }
}
package com.example.tensorflowlitetest.view.mediaPipe

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tensorflowlitetest.components.TopAppBarCustom
import com.example.tensorflowlitetest.components.models.ListModel
import com.example.tensorflowlitetest.utils.getModelList
import com.example.tensorflowlitetest.view.mediaPipe.viewModel.CameraViewModelMediaPipe
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectDetectionViewMediaPipe(
    navController: NavController,
    vm: CameraViewModelMediaPipe, //View model injected from Hilt
    scaleFactor:Float = 0.4f, //0.4f,
    usedFromVideoView:Boolean = false
) {
    val TAG = "ObjectDetectionView"

    //Result state from view model
    val uiState = vm.uiState.collectAsState()
    val bitmapPhoto = uiState.value.photoImage
    val detectedObjectArray = uiState.value.results
    val photoRotation = uiState.value.photoRotation

    //Config state from view model
    val configState = vm.configState.collectAsState()
    val scoreThreshold = configState.value.scoreThreshold
    val maxResults = configState.value.maxResults
    val modelIdState = configState.value.modelId

    //Slider state to change score and max result
    var sliderPositionScore by remember { mutableFloatStateOf(scoreThreshold) }
    var sliderPositionMaxRes by remember { mutableFloatStateOf(maxResults.toFloat()) }

    //Available object detection model list
    val modelList:List<ListModel> = getModelList("mediaPipe")
    var showModelList by remember {
        mutableStateOf(false)
    }

    val textMeasurer = rememberTextMeasurer()

    fun modelClickHandler(id: String){
        vm.setModelId(id.toInt())
    }

    Scaffold(
        topBar = {
            if(!usedFromVideoView) {
                TopAppBarCustom(navController = navController)
            }
        }

    ) {

            if (bitmapPhoto !== null) {

                Column {

                    //Slider to change score threshold
                    Column {
                        Slider(
                            value = sliderPositionScore,
                            onValueChange = { sliderPositionScore = Math.round(it*10).toFloat()/10.0f },
                            onValueChangeFinished = {vm.setScoreThreshold(sliderPositionScore)},
                            valueRange = 0f..1f,
                            steps = 10
                        )
                        Text(text = "Score thr: $sliderPositionScore")
                    }

                    //Slider to change max results
                    Column {
                        Slider(
                            value = sliderPositionMaxRes,
                            onValueChange = { sliderPositionMaxRes = it },
                            onValueChangeFinished = {vm.setMaxResults(sliderPositionMaxRes.toInt()) },
                            valueRange = 1f..10f,
                            steps = 10
                        )
                        Text(text = "Max res: ${sliderPositionMaxRes.toInt()}")
                    }

                    //Change model
                    Column {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Text("Model list")
                            IconButton(onClick = { showModelList = !showModelList }) {
                                Icon(
                                    if (!showModelList) Icons.Default.List else Icons.Default.Close,
                                    contentDescription = "models list"
                                )
                            }
                        }

                        if (showModelList){
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                items(
                                    items = modelList,
                                    key = { singleObject -> singleObject.id }
                                ) { row ->

                                    Surface(
                                        onClick = { modelClickHandler(row.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .background(color = if (Integer.parseInt(row.id) == modelIdState) Color.Red else Color.Transparent)
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .padding(start = 5.dp),
                                                text = row.text
                                            )
                                        }
                                    }

                                    Divider(thickness = 2.dp)
                                }
                            }
                        }
                    }

                    if (!showModelList) {
                        Column {

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
                                    if (photoRotation != 0) {
                                        left = -240f
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
                                            if (detectedObjectArray.size > 0) {
                                                val rectangles =
                                                    detectedObjectArray.get(0).detections()
                                                for (resultIdx in rectangles.indices) {

                                                    val category =
                                                        rectangles[resultIdx].categories()[0].categoryName()
                                                    val score =
                                                        round(rectangles[resultIdx].categories()[0].score() * 100) / 100
                                                    val boundingBox =
                                                        rectangles[resultIdx].boundingBox()
                                                    val top = boundingBox.top * scaleFactor
                                                    val bottom = boundingBox.bottom * scaleFactor
                                                    val left = boundingBox.left * scaleFactor
                                                    val right = boundingBox.right * scaleFactor

                                                    val categoryText = "$category, score: $score"
                                                    //Log.d(TAG, "object $categoryText")


                                                    //Create a rect around detected object
                                                    val path = Path()
                                                    path.moveTo(left, top)
                                                    path.lineTo(right, top)
                                                    path.lineTo(right, bottom)
                                                    path.lineTo(left, bottom)
                                                    path.close()
                                                    drawPath(
                                                        path,
                                                        color = Color.Red,
                                                        style = Stroke(width = 10f)
                                                    )

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

                                                    drawText(
                                                        textResult,
                                                        topLeft = Offset(
                                                            x = left,
                                                            y = top
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }
                            )
                        }
                    }
                }

            } else {
                Text(text = "No image available", modifier = Modifier)
            }


    }
}
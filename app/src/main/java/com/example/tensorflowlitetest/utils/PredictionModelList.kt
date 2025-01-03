package com.example.tensorflowlitetest.utils

import com.example.tensorflowlitetest.components.models.ListModel

fun getNameFromId(modelType: String, modelid: Int): String{

    val modelList = getModelList(modelType)
    val modelFond = modelList.first{it -> Integer.parseInt(it.id) == modelid}
    return modelFond.text
}

fun getModelList(modelType: String): List<ListModel> {

    if(modelType.equals("tfLite")) {
        return listOf(
            ListModel(
                id = "0",
                text = "tfLite/mobilenetv1.tflite"
            ),
            ListModel(
                id = "1",
                text = "tfLite/efficientdet-lite0.tflite"
            ),
            ListModel(
                id = "2",
                text = "tfLite/efficientdet-lite1.tflite"
            ),
            ListModel(
                id = "3",
                text = "tfLite/efficientdet-lite2.tflite"
            )
        )
    }

    if(modelType.equals("mediaPipe")){
        return listOf(
            ListModel(
                id = "0",
                text = "mediaPipe/efficientdet-lite0.tflite"
            ),
            ListModel(
                id = "1",
                text = "mediaPipe/coco2017_original.tflite"
            ),
            ListModel(
                id = "2",
                text = "mediaPipe/model_original_voc_2012.tflite"
            )
        )
    }

    return listOf()
}
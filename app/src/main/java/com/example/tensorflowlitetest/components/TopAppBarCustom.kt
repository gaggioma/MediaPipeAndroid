package com.example.tensorflowlitetest.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tensorflowlitetest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarCustom(navController: NavController){

    val tfIsReady:Boolean = true

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            if(tfIsReady){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                     Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = R.drawable.object_detection_icon),
                        contentDescription = "object_detection_icon"
                    )
                }
            }else{
                CircularProgressIndicator(
                    modifier = Modifier.width(20.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        },
        actions = {
            IconButton(onClick = {navController.navigate("videoViewMediaPipe") }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "prediction model"
                )
            }

            TextButton(onClick = {navController.navigate("gestureView") }) {
                Text("👋")
            }


        }
    )

}
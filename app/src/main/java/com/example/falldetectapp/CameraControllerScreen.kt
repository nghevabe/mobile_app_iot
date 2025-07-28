package com.example.falldetectapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import kotlinx.coroutines.delay

class CameraControllerScreen() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val myIntent = intent // gets the previously created intent
            val rootUrl = myIntent.getStringExtra("url")

            FallDetectAppTheme {
                Log.d("ZZZ_VAN", "rootUrl is: $rootUrl")
                BodyCameraController(rootUrl ?: "")
            }

        }
    }
}

@Composable
fun BodyCameraController(rootUrl: String) {

    // http://192.168.1.109/cam-lo.jpg
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color("#f2f4f4".toColorInt()))
    ) {

        val camLow = "$rootUrl/cam-lo.jpg"
        val camMid = "$rootUrl/cam-mid.jpg"

        var counter1 by remember { mutableIntStateOf(0) }
        var counter2 by remember { mutableIntStateOf(0) }
        var link1 by remember { mutableStateOf("") }
        var link2 by remember { mutableStateOf("") }

        if (counter1 < 500) {
            link1 = if (counter1 % 2 == 0) {
                camLow
            } else {
                camMid
            }
        }

        if (counter2 in 1..500) {
            link2 = if (counter2 % 2 == 0) {
                camLow
            } else {
                camMid
            }
        }

        Log.d("ZZZ_VAN", "ZZZ is: $link1")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(text = "Camera 01 Streaming")
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                Modifier.padding(16.dp).fillMaxWidth().height(300.dp)
                    .border(
                        width = 4.dp,
                        color = Color("#000000".toColorInt()),
                        shape = RoundedCornerShape(12.dp)
                    )

            ) {
                if (link1.isNotEmpty()) {

                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(link1)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        onLoading = { /* Show loading spinner */ },
                        onError = { /* Handle error */ },
                        onSuccess = { counter2++ },
                    )

                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(link2)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        onLoading = { /* Show loading spinner */ },
                        onError = { /* Handle error */ },
                        onSuccess = { counter1++ },
                    )


                }
            }
        }


    }
}

@Preview
@Composable
fun CamPreview() {
    BodyCameraController("")
}
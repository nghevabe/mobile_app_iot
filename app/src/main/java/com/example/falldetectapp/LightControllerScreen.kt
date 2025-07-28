package com.example.falldetectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database

class LightControllerScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FallDetectAppTheme {
                BodyLightController()
            }

        }
    }
}

@Composable
fun BodyLightController() {

    val fireBase = FirebaseHandle()

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var selectedColor by remember { mutableStateOf("#FFFFFF") }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Light Living Room",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = Color("#f0f3f4".toColorInt()),
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(selectedColor.toColorInt()))
        ) {

        }
        Spacer(modifier = Modifier.height(100.dp))
        Text(modifier = Modifier.clickable {
            fireBase.sendSignal(0, 0, 0)
            selectedColor = "#FFFFFF"
        }, text = "Turn Off")
        Spacer(modifier = Modifier.height(100.dp))
        Row {
            ItemColor("#E03C31") {
                // Red (255, 0, 0)
                fireBase.sendSignal(255, 0, 0)
                selectedColor = "#E03C31"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#48c9b0") {
                // Green (0, 255, 0)
                fireBase.sendSignal(0, 255, 0)
                selectedColor = "#48c9b0"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#5dade2") {
                // Blue (0, 0, 255)
                fireBase.sendSignal(0, 0, 255)
                selectedColor = "#5dade2"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#8e44ad") {
                // Violet (255, 0, 255)
                fireBase.sendSignal(255, 0, 255)
                selectedColor = "#8e44ad"
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            ItemColor("#00FFFF") {
                // Aqua (0, 255, 255)
                fireBase.sendSignal(255, 255, 0)
                selectedColor = "#00FFFF"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#f4d03f") {
                // Yellow (244, 208, 63)
                fireBase.sendSignal(244, 208, 63)
                selectedColor = "#f4d03f"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#d35400") {
                // Orange (230, 126, 34)
                fireBase.sendSignal(230, 126, 34)
                selectedColor = "#d35400"
            }
            Spacer(modifier = Modifier.width(24.dp))
            ItemColor("#ffffff") {
                // White (255, 255, 255)
                fireBase.sendSignal(255, 255, 255)
                selectedColor = "#ffffff"
            }
        }
    }
}

@Composable
fun ItemColor(colorCode: String, event: () -> Unit) {
    var x = 0
    Box(
        Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color("#f0f3f4".toColorInt()))
            .clickable {
                event()
            }
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .size(32.dp)
                .clip(RoundedCornerShape(322.dp))
                .background(Color(colorCode.toColorInt()))
        ) {

        }
    }
}

@Preview
@Composable
fun LightPreview() {
    BodyLightController()
}
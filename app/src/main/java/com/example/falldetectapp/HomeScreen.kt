package com.example.falldetectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue


class HomeScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val myIntent = intent // gets the previously created intent
            val user = myIntent.getStringExtra("username")
            FallDetectAppTheme {
                BodyHome(user ?: "")
            }


        }
    }
}

@Composable
private fun BodyHome(user: String) {

    val mutableList = remember { mutableListOf("") }
    val lstDevice = remember { mutableStateOf(mutableList) }
    var isLoadList by remember { mutableStateOf(false) }

    var isGetUrl = false
    var rootUrl = ""


    val database = Firebase.database
    val root = database.getReference("fsb_smart_home")

    root
        .child("user")
        .child(user)
        .child("device")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("ZZZ_VAN", "Change")
                lstDevice.value.clear()
                for (dSnapshot in dataSnapshot.children) {
                    val deviceName = dSnapshot.key ?: ""
                    lstDevice.value.add(deviceName)
//                    Log.d("ZZZ_VAN", deviceName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("XXX_VAN", "Failed to read value.", error.toException())
            }
        })

    root
        .child("user")
        .child(user)
        .child("device")
        .child("cam_01")
        .child("ip")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (!isGetUrl) {
                    rootUrl = dataSnapshot.getValue<String>() ?: ""
                    isGetUrl = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("XXX_VAN", "Failed to read value.", error.toException())
            }
        })

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color("#f2f4f4".toColorInt()))
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            modifier = Modifier.clickable {
                context.startActivity(
                    Intent(
                        context,
                        CallTestScreen::class.java
                    )
                )
            },
            text = "Home",
            fontSize = 24.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
            Text(
                text = "Devices", fontSize = 20.sp,
                color = Color.Black,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                AddNewButton(modifier = Modifier.align(Alignment.CenterEnd))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Box(
            Modifier
                .fillMaxWidth()

                .clip(RoundedCornerShape(4.dp))
                .background(Gray).clickable {
                    isLoadList = false
                }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color("#778899".toColorInt()))
                    .padding(vertical = 6.dp),
                text = "List Device",
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Handler().postDelayed({
            isLoadList = true
            Log.d("ZZZ_VAN","lstDevice size: "+lstDevice.value.size)
        }, 1000)

        if (isLoadList) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                items(lstDevice.value.size) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        var painter: Painter = painterResource(id = R.drawable.ic_light)
                        var event: () -> Unit? = {}

                        if (lstDevice.value[it].contains("light")
                            ||
                            lstDevice.value[it].contains("LIGHT")) {
                            painter = painterResource(id = R.drawable.ic_light)
                            event = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        LightControllerScreen::class.java
                                    )
                                )
                            }
                        }

                        if (lstDevice.value[it].contains("door")) {
                            painter = painterResource(id = R.drawable.ic_door)
                            event = {
                                context.startActivity(
                                    Intent(
                                        context,
                                        DoorControllerScreen::class.java
                                    )
                                )
                            }
                        }

                        if (lstDevice.value[it].contains("cam")) {
                            painter = painterResource(id = R.drawable.ic_light)
                            event = {
                                val myIntent = Intent(
                                    context,
                                    CameraControllerScreen::class.java
                                )
                                myIntent.putExtra("url", rootUrl)
                                myIntent.putExtra("username", user)
                                context.startActivity(myIntent)
                            }
                        }

                        if (lstDevice.value[it].contains("fan")) {
                            painter = painterResource(id = R.drawable.ic_fan)
                            event = {

                            }
                        }

                        CardDeviceItem(lstDevice.value[it], painter) {
                            event()
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

//        Row {
//            CardDeviceItem("Light_01", painterResource(id = R.drawable.ic_light)) {
//                context.startActivity(Intent(context, LightControllerScreen::class.java))
//            }
//            Spacer(modifier = Modifier.width(32.dp))
//            CardDeviceItem("Door_01", painterResource(id = R.drawable.ic_door)) {
//                context.startActivity(Intent(context, DoorControllerScreen::class.java))
//            }
//        }
//
//        Spacer(modifier = Modifier.height(28.dp))
//
//        Row {
//            CardDeviceItem("Fan_01", painterResource(id = R.drawable.ic_fan)) {
//                // todo
//            }
//            Spacer(modifier = Modifier.width(32.dp))
//            CardDeviceItem("Camera_01", painterResource(id = R.drawable.ic_light)) {
//
//                val myIntent = Intent(
//                    context,
//                    CameraControllerScreen::class.java
//                )
//                myIntent.putExtra("url", rootUrl)
//                myIntent.putExtra("username", user)
//                context.startActivity(myIntent)
//
//            }
//        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun AddNewButton(modifier: Modifier) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.LightGray)
            .clickable {
                context.startActivity(Intent(context, AddDeviceScreen::class.java))
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Add, "", tint = Color("#6B5342".toColorInt()))
            Text(text = "Add New")
        }
    }
}

@Composable
private fun CardDeviceItem(deviceName: String, painter: Painter, event: () -> Unit? = {}) {

    Box(
        modifier = Modifier
            .fillMaxWidth().padding(end = 16.dp)
//            .width(172.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { event() }
    ) {
        Column(Modifier.padding(start = 8.dp, top = 8.dp)) {
            Text(
                text = "" + deviceName,
                fontSize = 14.sp,
                color = Color("#778899".toColorInt()),
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row() {
                Image(
                    painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(72.dp)
                )
                Switch(
                    modifier = Modifier
                        .scale(0.7f)
                        .padding(start = 36.dp, top = 36.dp),
                    checked = true,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.LightGray,
                        uncheckedThumbColor = Color.Blue,
                        uncheckedTrackColor = Color.LightGray,
                    ), onCheckedChange = {

                    })
            }

        }
    }

}

//@Composable
//private fun CardDeviceItem(deviceName: String, painter: Painter, event: () -> Unit? = {}) {
//
//    Box(
//        modifier = Modifier
//            .width(160.dp)
//            .height(180.dp)
//            .clip(RoundedCornerShape(8.dp))
//            .background(Color.White)
//            .clickable { event() }
//    ) {
//        Column(Modifier.padding(start = 8.dp, top = 8.dp)) {
//            Image(painter,
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(56.dp))
//
//            Spacer(modifier = Modifier.height(6.dp))
//            Text(text = "" + deviceName, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
//            Text(text = "Connected", color = Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
//            Spacer(modifier = Modifier.height(28.dp))
//            Switch(
//                modifier = Modifier.scale(0.7f).padding(bottom = 28.dp),
//                checked = true,
//                colors = SwitchDefaults.colors(
//                    checkedThumbColor = Color.White,
//                    checkedTrackColor = Color.LightGray,
//                    uncheckedThumbColor = Color.Blue,
//                    uncheckedTrackColor = Color.LightGray,
//                ), onCheckedChange = {
//
//                })
//        }
//    }
//
//}




@Preview
@Composable
fun Layout() {
    BodyHome("XXX")
}
package com.example.falldetectapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.thanosfisherman.wifiutils.WifiUtils
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

//class AddDeviceScreen : FragmentActivity() {
class AddDeviceScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FallDetectAppTheme {
                AddDeviceBody()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDeviceBody() {

    val context = LocalContext.current

    var deviceIdInput by remember { mutableStateOf("") }
    var wifiNameInput by remember { mutableStateOf("") }
    var wifiPassInput by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    var isShowProcessDialog by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }

    val mutableList = remember { mutableListOf(DeviceData()) }
    val lstDevice = remember { mutableStateOf(mutableList) }

    val wifiPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            lstDevice.value.clear()
            isShowProcessDialog = true
            WifiUtils.withContext(context)
                .connectWith(deviceName, deviceIdInput)
                .setTimeout(40000)
                .onConnectionResult(object : ConnectionSuccessListener {
                    override fun success() {
                        Toast.makeText(context, "LINH WIFI SUCCESS!", Toast.LENGTH_SHORT).show()

                        Handler().postDelayed({
                            shareWifi(wifiNameInput, wifiPassInput) {
                                isShowProcessDialog = false
                                Toast.makeText(
                                    context,
                                    "Connect Device Successfull!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                reconnectWifi(context, deviceName)

                            }
                        }, 3000)
                    }

                    override fun failed(errorCode: ConnectionErrorCode) {
                        isShowProcessDialog = false
                        Toast.makeText(
                            context,
                            "LINH WIFI FAIL!$errorCode",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                .start()
        }
    )

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false)

    if (isShowProcessDialog) {

        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                openBottomSheet = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                CircularProgressIndicatorSample()
            }
        }

    }

    // Sheet content
    if (openBottomSheet) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                openBottomSheet = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "Connect With Device",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = deviceIdInput,
                        onValueChange = { deviceIdInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        label = { Text("Input Device Id") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = wifiNameInput,
                        onValueChange = { wifiNameInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        label = { Text("Input Your Wifi") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = wifiPassInput,
                        onValueChange = { wifiPassInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        label = { Text("Input Your Password") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color("#6B5342".toColorInt()))
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .clickable {
//                            wifiPermissionLauncher.launch(
//                                arrayOf(
//                                    Manifest.permission.WRITE_SETTINGS,
//                                    Manifest.permission.ACCESS_WIFI_STATE,
//                                    Manifest.permission.CHANGE_WIFI_STATE,
//                                    Manifest.permission.CHANGE_NETWORK_STATE,
//                                    Manifest.permission.ACCESS_NETWORK_STATE,
//                                )
//                            )

                            lstDevice.value.clear()
                            isShowProcessDialog = true
                            WifiUtils.withContext(context)
                                .connectWith(deviceName, deviceIdInput)
                                .setTimeout(40000)
                                .onConnectionResult(object : ConnectionSuccessListener {
                                    override fun success() {

                                        Handler().postDelayed({
                                            shareWifi(wifiNameInput, wifiPassInput) {
                                                isShowProcessDialog = false
                                                Toast.makeText(
                                                    context,
                                                    "Connect Device Successfull!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                isConnected = true
                                                reconnectWifi(context, deviceName)

                                            }
                                        }, 3000)
                                    }

                                    override fun failed(errorCode: ConnectionErrorCode) {
                                        isShowProcessDialog = false
                                        Toast.makeText(
                                            context,
                                            "LINH WIFI FAIL!$errorCode",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                                .start()

                            scope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        openBottomSheet = false
                                    }
                                }
                        }) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .align(Alignment.Center),
                            text = "Connect",
                            fontSize = 14.sp,
                            color = Color.LightGray,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


    }

    // -------

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { _ ->
            lstDevice.value.clear()
            scanWifi(context) { data ->
                isScanning = false
                lstDevice.value = data ?: SnapshotStateList()
                Toast.makeText(
                    context,
                    "Scanning Successful: " + lstDevice.value.size,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    //

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color("#f2f4f4".toColorInt()))
            .padding(bottom = 250.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Add Device", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(42.dp))
        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .border(
                    width = 2.dp,
                    color = Color("#6B5342".toColorInt()),
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
        ) {

            if (isScanning){
                Box(Modifier.align(Alignment.Center)) {
                    CircularProgressIndicator()
                }
            }

            if (isConnected == false) {
                if (lstDevice.value.size > 0) {
                    Log.d("ZZZ_VAN", "Load List")

                    LazyColumn(modifier = Modifier.fillMaxHeight()) {

                        items(items = lstDevice.value, itemContent = { item ->
                            if (item.deviceName?.contains("MSE_IOT") == true) {

                                CardDeviceItem(
                                    item.deviceName,
                                    painterResource(id = R.drawable.ic_light)
                                ) {
                                    deviceName = item.deviceName
                                    openBottomSheet = true
                                    Toast.makeText(context, "Please Waiting", Toast.LENGTH_SHORT)
                                        .show()

                                }

                            }
                        })

                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(42.dp))

    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 32.dp)) {
        Row(Modifier.align(Alignment.BottomCenter)) {
            Box(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color("#6B5342".toColorInt()))
                .clickable {
                    isScanning = true

//                    locationPermissionLauncher.launch(
//                        arrayOf(
//                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        )
//                    )

                    lstDevice.value.clear()
                    scanWifi(context) { data ->
                        isScanning = false
                        lstDevice.value = data ?: SnapshotStateList()
                        Toast.makeText(
                            context,
                            "Scanning Successful",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .align(Alignment.Center),
                    text = "Scan",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                )
            }

        }
    }

}

fun shareWifi(id: String, pass: String, event: () -> Unit?) {
    val gfgPolicy =
        ThreadPolicy
            .Builder()
            .permitAll()
            .build()
    StrictMode.setThreadPolicy(gfgPolicy)

    val url = URL("http://192.168.4.1/wifi?id=$id&pass=$pass")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"  // optional default is GET

        inputStream.bufferedReader().use {
            if (it.lines().count() >= 0) {
                event()
            }
//            it.lines().forEach { line ->
//                Log.d("XXX_VAN", "line: $line")
//            }
        }
    }

}

fun scanWifi(context: Context, event: (SnapshotStateList<DeviceData>?) -> Unit? = {}) {

    val lstData: SnapshotStateList<DeviceData> = SnapshotStateList()
    val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager

    val wifiScanReceiver = object : BroadcastReceiver() {
        // SCAN_RESULTS_AVAILABLE_ACTION
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                } else {
                    val results = wifiManager.scanResults

                    for (item in results) {
                        val device = (DeviceData(
                            deviceName = item.SSID.toString(),
                            deviceType = "light"
                        ))
                        lstData.add(device)
                    }

                    Log.d("ZZZ_VAN", "lstItem: " + lstData.size)
                    event(lstData)

                }

            }
        }
    }

    val intentFilter = IntentFilter()
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    context.registerReceiver(wifiScanReceiver, intentFilter)

    val success = wifiManager.startScan()
    if (!success) {
        // scan failure handling
    }

}

data class DeviceData(
    val deviceName: String? = "",
    val devicePass: String? = "",
    val deviceType: String? = "",
)

@Composable
fun CircularProgressIndicatorSample() {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Connecting to Device")
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun CardDeviceItem(deviceName: String, painter: Painter, event: () -> Unit? = {}) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { event() }
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Image(
                painter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp)
            )
            Column(Modifier.padding(start = 8.dp, top = 8.dp)) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "" + deviceName,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "Available",
                    color = Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }
        }
    }

}

fun reconnectWifi(context: Context, deviceName: String){
    val fireBase = FirebaseHandle()
    WifiUtils.withContext(context)
        .connectWith("linhth8", "nose4191")
        .setTimeout(40000)
        .onConnectionResult(object : ConnectionSuccessListener {
            override fun success() {
                fireBase.sendSignalCreateLight(0,0,0, deviceName)
                Handler().postDelayed({
                    val myIntent = Intent(
                        context,
                        HomeScreen::class.java
                    )
                    myIntent.putExtra("username", "linhth8")
                    context.startActivity(myIntent)
                }, 500)


            }

            override fun failed(errorCode: ConnectionErrorCode) {
                Toast.makeText(
                    context,
                    "Connect WIFI FAIL!$errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        .start()

}

@Preview
@Composable
fun PreviewAddDevice() {
    AddDeviceBody()
}
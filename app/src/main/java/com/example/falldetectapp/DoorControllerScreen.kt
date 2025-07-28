package com.example.falldetectapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.fragment.app.FragmentActivity

import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.google.firebase.Firebase
import com.google.firebase.database.database
import javax.security.auth.callback.Callback

class DoorControllerScreen : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            FallDetectAppTheme {
                DoorControllerBody()
            }


        }
    }
}

@Composable
fun DoorControllerBody() {
    val fireBase = FirebaseHandle()

    val context = LocalContext.current as FragmentActivity
    val biometricManager = BiometricManager.from(context)
    val canAuthenticateWithBiometrics = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            Log.e("TAG", "Device does not support strong biometric authentication")
            false
        }
    }

    var doorState by remember { mutableStateOf("Closing") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color("#f2f4f4".toColorInt())),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Door_01", fontSize = 24.sp)
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
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Text(text = doorState, modifier = Modifier.align(Alignment.Center), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(42.dp))

        Row {
            Box(modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color("#6B5342".toColorInt()))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {

                    if (canAuthenticateWithBiometrics) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            authenticateWithBiometric(context, "ON"){
                                fireBase.sendSignalDoor("ON")
                                doorState = "Opening"
                                Toast.makeText(context, "Open Door Successful", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.d("XXX_VAN","Biometric Error")
                    }

                }) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.Center),
                    text = "Open",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                )
            }
            Spacer(modifier = Modifier.width(42.dp))
            Box(modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color("#6B5342".toColorInt()))
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable {

                    if (canAuthenticateWithBiometrics) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            authenticateWithBiometric(context, "OFF"){
                                fireBase.sendSignalDoor("OFF")
                                doorState = "Closing"
                                Toast.makeText(context, "Close Door Successful", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Log.d("XXX_VAN","Biometric Error")
                    }

                }) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.Center),
                    text = "Close",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                )
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.P)
fun authenticateWithBiometric(context: FragmentActivity, signal: String, event: () -> Unit? = {}) {

    val executor = context.mainExecutor
    val biometricPrompt = BiometricPrompt(
        context,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                event()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e("TAG", "onAuthenticationError")
                //TODO Handle authentication errors.
            }

            override fun onAuthenticationFailed() {
                Log.e("TAG", "onAuthenticationFailed")
                //TODO Handle authentication failures.
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setDescription("Place your finger the sensor or look at the front camera to authenticate.")
        .setNegativeButtonText("Cancel")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        .setConfirmationRequired(true)
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@Preview
@Composable
fun LayoutDoor() {
    DoorControllerBody()
}

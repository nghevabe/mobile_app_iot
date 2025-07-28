package com.example.falldetectapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class CallTestScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val mContext = LocalContext.current
            FirebaseApp.initializeApp(mContext)

            FallDetectAppTheme {
                Greetings(
                    modifier = Modifier.padding(100.dp)
                )
            }

            connectNodes(mContext)
        }
    }
}

fun connectNodes(context: Context) {
    // [START write_message]
    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("fsb_emotion_detech/alert_system")

    // [END write_message]

    // [START read_message]
    // Read from the database
    myRef.child("negative").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = dataSnapshot.getValue<String>()

            if (value == "1"){
                makePhoneCalls("913171996",context)
            }
            Log.d("XXX_VAN", "ZZZ is: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("XXX_VAN", "Failed to read value.", error.toException())
        }
    })
    // [END read_message]
}

@Composable
fun Greetings(modifier: Modifier = Modifier) {
    Text(
        text = "Empty Screen",
        modifier = modifier.clickable {
            val phone_number = "0913171996"

            // Getting instance of Intent with action as ACTION_CALL
            val phone_intent = Intent(Intent.ACTION_CALL)

            // Set data of Intent through Uri by parsing phone number
            phone_intent.data = Uri.parse("tel:$phone_number")

        }
    )
}

fun makePhoneCalls(customerPhone:String, context: Context) {
    try {
        val formattedPhone = "0$customerPhone"
        val intent = Intent(Intent.ACTION_CALL)
        val phoneUri = Uri.parse("tel:$formattedPhone")
        intent.data = phoneUri

        val permission = Manifest.permission.CALL_PHONE

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error making phone call", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreviews() {
    FallDetectAppTheme {
//        Greeting("Android")
    }
}
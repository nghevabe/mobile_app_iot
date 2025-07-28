package com.example.falldetectapp

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.falldetectapp.ui.theme.FallDetectAppTheme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import java.util.Locale
import java.util.Objects
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.core.app.ActivityCompat.startActivityForResult
import java.util.*
import androidx.compose.ui.res.painterResource


class AssistantScreen : ComponentActivity() {
    lateinit var mediaPlayer: MediaPlayer
    private var speak: TextToSpeech? = null
    val REQUEST_CODE_SPEECH_INPUT = 1
    var responseVoice by mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            speak = TextToSpeech(applicationContext) { status ->
                if (status != TextToSpeech.ERROR) {
                    speak?.setLanguage(Locale.getDefault())
                }
            }

            mediaPlayer = MediaPlayer()
            val mContext = LocalContext.current
            FirebaseApp.initializeApp(mContext)

            FallDetectAppTheme {
                LayoutAssistant(mContext, responseVoice, mediaPlayer)
            }

            getResponse(mediaPlayer)
        }
    }


    // on below line we are calling on activity result method.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fireBase = FirebaseHandle()
        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {

                // in that case we are extracting the
                // data from our array list
                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                // on below line we are setting data
                // to our output text view.
                val result = Objects.requireNonNull(res)[0]
                responseVoice = result
                fireBase.sendRequest(responseVoice)

                Log.d("XXX_VAN", "ZZZ is: $result")
            }
        }
    }

}

fun getResponse(mediaPlayer: MediaPlayer) {
    // [START write_message]
    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("smart_home_assistant/virtual_assistant")
    val fireBase = FirebaseHandle()

    // [END write_message]

    // [START read_message]
    // Read from the database
    myRef.child("response").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            val value = dataSnapshot.getValue<String>()

            if (!value.isNullOrEmpty() && value.contains(".mp3")) {
                playMedia(mediaPlayer, value)
//                Thread.sleep(1000)
                fireBase.clearSignal("response")
            }

            Log.d("XXX_VAN", "ZZZ is: $value")
        }

        // "https://stream.nct.vn/NhacCuaTui172/GiacMoTrua-ThuyChi_39v3a.mp3?st=S9kYOg3OtAEbvCDSBkXQHw&e=1752362217&a=1&p=0&r=4d8e3e6454e8c263c4be94fa9fe05ed1"
        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("XXX_VAN", "Failed to read value.", error.toException())
        }
    })
    // [END read_message]
}

@Composable
fun LayoutAssistant(context: Context, responseVoice: String, mediaPlayer: MediaPlayer) {

    // Tôi muốn nghe bài hát giấc mơ trưa
    var input by remember { mutableStateOf("") }
    val fireBase = FirebaseHandle()

    if (responseVoice.isNotEmpty()){
        input = responseVoice
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.width(300.dp),
                value = input,
                onValueChange = { input = it },
                label = { Text( "Enter your request") },
            )
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                painter = painterResource(R.drawable.icon_mic),
                contentDescription = "print",
                Modifier.clickable {
                    mediaPlayer.reset()
                    speechInput(context)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = {
                    mediaPlayer.reset()
                    fireBase.sendRequest(input)
                    Thread.sleep(1000)
                    fireBase.clearSignal("request")
                }, colors = ButtonDefaults.textButtonColors()
            ) {
                Text("Send")
            }

            Spacer(modifier = Modifier.width(24.dp))

        }

    }
}

private fun speechInput(context: Context) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )

    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE,
        Locale.getDefault()
    )

    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

    val activity = context as Activity
    try {
        startActivityForResult(activity, intent, 1, null)
    } catch (e: Exception) {
        // on below line we are displaying error message in toast
        Toast
            .makeText(
                context, " " + e.message,
                Toast.LENGTH_SHORT
            )
            .show()
    }

}


fun playMedia(mediaPlayer: MediaPlayer, ulrMedia: String) {

    // on below line we are creating a variable for our audio url
//    var audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    var audioUrl = ulrMedia

    // on below line we are setting audio stream
    // type as stream music on below line.
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

    // on below line we are running a try
    // and catch block for our media player.
    try {
        // on below line we are setting audio
        // source as audio url on below line.
        mediaPlayer.setDataSource(audioUrl)

        // on below line we are
        // preparing our media player.
        mediaPlayer.prepare()

        // on below line we are
        // starting our media player.
        mediaPlayer.start()

    } catch (e: Exception) {

        // on below line we are handling our exception.
        e.printStackTrace()
    }
    // on below line we are displaying a toast message as audio player.


}

@Preview(showBackground = true)
@Composable
fun AssistantPreviews() {
    FallDetectAppTheme {
//        LayoutAssistant(LocalContext.current, "")
    }
}
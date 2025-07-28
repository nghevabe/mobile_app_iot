package com.example.falldetectapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
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

data class User(val userName: String? = "", val passWord: String? = "")

class LoginScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            FallDetectAppTheme {
                LoginBody()
            }
        }
    }
}

@Composable
fun LoginBody() {

    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        Image(
            painterResource(id = R.drawable.bg_1b),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(150.dp))
        Text(
            modifier = Modifier,
            text = "FSB Iot Smart Home",
            fontSize = 30.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(52.dp))

        var userInput by remember { mutableStateOf("linhth8") }
        var passInput by remember { mutableStateOf("123456") }

        TextField(
            value = userInput,
            placeholder = {Text(text = "Username")},
            onValueChange =  { value ->
                userInput = value
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(Icons.Filled.Person, "", tint = Gray)
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passInput,
            placeholder = {Text(text = "Password")},
            onValueChange = { value ->
                passInput = value
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(Icons.Filled.Lock, "", tint = Gray)
            },
        )

        Spacer(modifier = Modifier.height(86.dp))

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color("#6B5342".toColorInt()))
                .clickable {
                    checkLogin(context, userInput, passInput)
                }
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.Center),
                text = "Login",
                fontSize = 20.sp,
                color = Color.LightGray,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Row(Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 6.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Text(modifier = Modifier.padding(horizontal = 6.dp), text = "OR")
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 6.dp, end = 16.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            ItemSocial(painterResource(R.drawable.ic_fb))
            Spacer(modifier = Modifier.width(24.dp))
            ItemSocial(painterResource(R.drawable.ic_gg))
            Spacer(modifier = Modifier.width(24.dp))
            ItemSocial(painterResource(R.drawable.ic_apple))
        }
        Spacer(modifier = Modifier.height(42.dp))
        Text(text = "Don't have an account? Sign up", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun checkLogin(context: Context, inputUserName: String, inputPass: String) {

    val database = Firebase.database
    val myRef = database.getReference("fsb_smart_home")
    var isLoadData = false

    myRef.child("user").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {

            if (!isLoadData) {
                val pass = dataSnapshot
                    .child(inputUserName)
                    .child("password").getValue<String>() ?: ""

                if (inputPass == pass){

                    val myIntent = Intent(
                        context,
                        HomeScreen::class.java
                    )
                    isLoadData = true
                    myIntent.putExtra("username", inputUserName)
                    context.startActivity(myIntent)
                }

            }

        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w("XXX_VAN", "Failed to read value.", error.toException())
        }
    })

}

@Composable
fun ItemSocial(painter: Painter) {
    Box(
        Modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = Color("#f0f3f4".toColorInt()),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        )
    }
}

@Preview
@Composable
fun PreviewLogin() {
    LoginBody()
}
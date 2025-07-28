package com.example.falldetectapp

import com.google.firebase.Firebase
import com.google.firebase.database.database

class FirebaseHandle {

    private val database = Firebase.database

     fun sendSignalDoor(state: String) {
        val rootNode = database.getReference("fsb_smart_home")
            .child("user").child("linhth8").child("device")
        rootNode.child("door_01")
            .child("state").setValue(state)
    }

    fun sendSignal(signalRed: Int, signalGreen: Int, signalBlue: Int) {
        val rootNode = database.getReference("fsb_smart_home")
            .child("user").child("linhth8")
            .child("device").child("MSE_IOT_LIGHT_01")
        rootNode.child("red").setValue(signalRed)
        rootNode.child("green").setValue(signalGreen)
        rootNode.child("blue").setValue(signalBlue)
    }

    fun sendSignalCreateLight(signalRed: Int, signalGreen: Int, signalBlue: Int, deviceName: String) {
        val rootNode = database.getReference("fsb_smart_home")
            .child("user").child("linhth8")
            .child("device").child(deviceName)
        rootNode.child("red").setValue(signalRed)
        rootNode.child("green").setValue(signalGreen)
        rootNode.child("blue").setValue(signalBlue)
    }

    fun sendCallSignal(state: String) {
        val rootNode = database.getReference("fsb_smart_home")
            .child("user").child("linhth8").child("device")
        rootNode.child("cam_01")
            .child("state_fall").setValue(state)
    }

    fun sendRequest(request: String) {
        val rootNode = database.getReference("smart_home_assistant")
            .child("virtual_assistant")
        rootNode.child("request").setValue(request)
    }

    fun clearSignal(node: String) {
        val rootNode = database.getReference("smart_home_assistant")
            .child("virtual_assistant")
        rootNode.child(node).setValue("")
    }
}
package com.example.smackandroid.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.smackandroid.R
import com.example.smackandroid.service.NetworkConnection
import com.example.smackandroid.service.NetworkConnectionService
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    companion object {
        const val TAG="ChatActivity"
    }


    private var contactJID:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent=intent
        contactJID=intent.getStringExtra("jid")
        title=contactJID
    }


    fun sendMessage(view: View){
        if (NetworkConnectionService.getState()==NetworkConnection.ConnectionState.CONNECTED){
            Log.d(TAG, "The client is connected to the server,Sendint Message")
            val intent=Intent(NetworkConnectionService.SEND_MESSAGE)
            intent.putExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY,typedMessage.text.toString())
            intent.putExtra(NetworkConnectionService.BUNDLE_TO,contactJID)

            sendBroadcast(intent)
        }else{
            Toast.makeText(applicationContext,"Client is not connected to the server.Message is not sent",Toast.LENGTH_LONG).show()
        }
    }
}

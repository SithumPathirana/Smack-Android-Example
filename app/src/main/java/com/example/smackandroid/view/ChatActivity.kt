package com.example.smackandroid.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.smackandroid.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent=intent
        title=intent.getStringExtra("jid")
    }
}

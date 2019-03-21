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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.smackandroid.modal.Message





class ChatActivity : AppCompatActivity() {


    private var mBroadcastReceiver: BroadcastReceiver? = null



    companion object {
        const val TAG="ChatActivity"
    }


    private var contactJID:String?=null


    private var messagesList= arrayListOf<Message>()
    lateinit var messageListView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent=intent
        contactJID=intent.getStringExtra("jid")
        title=contactJID

        messageListView=messageList
        layoutManager=LinearLayoutManager(this)
        messageListView.layoutManager=layoutManager
        adapter= MessageAdapater(messagesList)
        messageListView.adapter=adapter
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        mBroadcastReceiver=object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val acion=intent?.action
                val from=intent?.getStringExtra(NetworkConnectionService.BUNDLE_FROM_JID)
                val body=intent?.getStringExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY)

                when(acion){
                    NetworkConnectionService.NEW_MESSAGE ->  recieveMessgesAndUpdateChatView(from!!,body!!)

                }
            }
        }

        val filter = IntentFilter(NetworkConnectionService.NEW_MESSAGE)
        registerReceiver(mBroadcastReceiver, filter)
    }


    fun sendMessage(view: View){
        if (NetworkConnectionService.getState()==NetworkConnection.ConnectionState.CONNECTED){
            Log.d(TAG, "The client is connected to the server,Sendint Message")
            val intent=Intent(NetworkConnectionService.SEND_MESSAGE)
            intent.putExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY,typedMessage.text.toString())
            intent.putExtra(NetworkConnectionService.BUNDLE_TO,contactJID)

            sendBroadcast(intent)

            val message = typedMessage.text.toString()
            val insertIndex = 0
            messagesList.add(insertIndex,Message(message,true) )
            adapter?.notifyItemInserted(insertIndex)


        }else{
            Toast.makeText(applicationContext,"Client is not connected to the server.Message is not sent",Toast.LENGTH_LONG).show()
        }
    }

    private fun recieveMessgesAndUpdateChatView(from:String,body:String){
        if (from==contactJID){
           Log.d(TAG,"New message recieved from $contactJID")
            Log.d(TAG,"Message Body $body")


            val insertIndex = 0
            messagesList.add(insertIndex,Message(body,false) )
            adapter?.notifyItemInserted(insertIndex)

        }else{
            Log.d(TAG,"Got a message from another jid : $contactJID")
        }

    }
}

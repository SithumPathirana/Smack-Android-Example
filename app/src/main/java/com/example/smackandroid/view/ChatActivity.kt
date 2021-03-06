package com.example.smackandroid.view

import android.app.Activity
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
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.example.smackandroid.modal.ChatMessage
import com.example.smackandroid.modal.Type
import com.example.smackandroid.util.RealPathUtil


class ChatActivity : AppCompatActivity() {


    private var mBroadcastReceiver: BroadcastReceiver? = null



    companion object {
        const val TAG="ChatActivity"
        const val FILE_SELECT_ACTIVITY_REQUEST_CODE=123
        const val FILE_TRANSFER_REQUEST_CODE=124
    }


    private var contactJID:String?=null


    private lateinit var messageBody: EditText
    private var messagesList= arrayListOf<ChatMessage>()
    lateinit var messageListView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val intent=intent
        contactJID=intent.getStringExtra("jid")
        title=contactJID
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        messageBody=findViewById(R.id.typedMessage)
        messageListView=messageList
        layoutManager=LinearLayoutManager(this)
        messageListView.layoutManager=layoutManager
        adapter= MessageAdapater(messagesList,this)
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

                when(acion){
                    NetworkConnectionService.NEW_MESSAGE ->  recieveMessgesAndUpdateChatView(intent)
                    NetworkConnectionService.UI_NEW_MESSAGE_FLAG -> updateChatUI(intent)

                }
            }
        }

        val filter = IntentFilter(NetworkConnectionService.NEW_MESSAGE)
        filter.addAction(NetworkConnectionService.UI_NEW_MESSAGE_FLAG)
        registerReceiver(mBroadcastReceiver, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

       when(item?.itemId){
           android.R.id.home ->   {
               val intent=Intent(this,ContactListActivity::class.java)
               startActivity(intent)
               finish()
           }
           R.id.contact_details ->   {
               Toast.makeText(this, "You clicked Contact Details", Toast.LENGTH_SHORT).show()
           }
           R.id.send_image ->  {
               val insertIndex = 0
               messagesList.add(insertIndex, ChatMessage("Send Image",Type.IMAGE_SENT,contactJID!!,"") )
               adapter?.notifyItemInserted(insertIndex)
           }
           R.id.transfer_file ->  {
               Log.d(TAG,"User wants to transfer a file")
               transferFile()
           }
           R.id.send_office -> {

           }

           R.id.send_file -> {
               Log.d(TAG,"User wants to send a file")
               sendFile()
           }

           R.id.logout -> {
               Log.d(TAG,"User is logging out")
               logoutUser()
           }
       }
        return true

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
            messagesList.add(insertIndex, ChatMessage(message,Type.SENT,contactJID!!,"") )
            adapter?.notifyItemInserted(insertIndex)
            messageBody.text.clear()
        }else{
            Toast.makeText(applicationContext,"Client is not connected to the server.Message is not sent",Toast.LENGTH_LONG).show()
        }
    }

    private fun recieveMessgesAndUpdateChatView(intent: Intent){

        val from=intent?.getStringExtra(NetworkConnectionService.BUNDLE_FROM_JID)
        val body=intent?.getStringExtra(NetworkConnectionService.BUNDLE_MESSAGE_BODY)

        if (from==contactJID){
           Log.d(TAG,"New message recieved from $contactJID")
            Log.d(TAG,"Message Body $body")


            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage(body, Type.RECEIVED,contactJID!!,"") )
            adapter?.notifyItemInserted(insertIndex)

        }else{
            Log.d(TAG,"Got a message from another jid : $contactJID")
        }

    }


    private fun updateChatUI(intent: Intent){
        val messageType=intent?.getStringExtra(NetworkConnectionService.BUNDLE_MESSAGE_TYPE)
        val attachmentPath=intent?.getStringExtra(NetworkConnectionService.BUNDLE_MESSAGE_ATTACHMENT_PATH)
        Log.d(TAG,"Bundle message type is  : $messageType")
        Log.d(TAG,"Bundle message attachment path $attachmentPath")

        if (messageType==Type.IMAGE_SENT.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.IMAGE_SENT,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.IMAGE_RECEIVED.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.IMAGE_RECEIVED,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.VIDEO_SENT.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.VIDEO_SENT,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.AUDIO_SENT.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.AUDIO_SENT,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.AUDIO_RECEIVED.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.AUDIO_RECEIVED,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }



        if (messageType==Type.VIDEO_RECEIVED.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.VIDEO_RECEIVED,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.PDF_SENT.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.PDF_SENT,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.PDF_RECEIVED.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.PDF_RECEIVED,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.OTHER_SENT.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.OTHER_SENT,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }

        if (messageType==Type.OFFICE_RECEIVED.toString()){
            val insertIndex = 0
            messagesList.add(insertIndex, ChatMessage("", Type.OTHER_RECEIVED,contactJID!!,attachmentPath) )
            adapter?.notifyItemInserted(insertIndex)
        }
    }


    private fun sendFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_SELECT_ACTIVITY_REQUEST_CODE)
    }

    private fun transferFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_TRANSFER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){

            var filePath: String? = null

            when(requestCode){
                FILE_SELECT_ACTIVITY_REQUEST_CODE -> {
                    val selectedImage=data?.data

                    // Save the file in a directory in the device and send it
                    filePath=RealPathUtil.getRealPath(this,selectedImage!!)
                    Log.d(TAG,"File Uri is $selectedImage")
                    Log.d(TAG,"File path is $filePath")

                    if (filePath==null){
                        Log.d(TAG,"File uri is null")
                        return
                    }

                    NetworkConnectionService.getConnection().sendFile(filePath,contactJID!!)
                }
                FILE_TRANSFER_REQUEST_CODE->{
                    val selectedFile=data?.data
                    filePath=RealPathUtil.getRealPath(this,selectedFile!!)
                    Log.d(TAG,"File Uri is $selectedFile")
                    Log.d(TAG,"File path is $filePath")

                    if (filePath==null){
                        Log.d(TAG,"File uri is null")
                        return
                    }

                    NetworkConnectionService.getConnection().transferFile(filePath,contactJID!!)
                }
            }
        }
    }

    private fun logoutUser(){
       NetworkConnectionService.getConnection().logoutUser()
    }
}

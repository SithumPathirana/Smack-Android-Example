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
import com.example.smackandroid.modal.Message
import com.example.smackandroid.modal.Type
import com.example.smackandroid.util.RealPathUtil


class ChatActivity : AppCompatActivity() {


    private var mBroadcastReceiver: BroadcastReceiver? = null



    companion object {
        const val TAG="ChatActivity"
        const val FILE_SELECT_ACTIVITY_REQUEST_CODE=123
    }


    private var contactJID:String?=null


    private lateinit var messageBody: EditText
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        messageBody=findViewById(R.id.typedMessage)
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
               messagesList.add(insertIndex,Message("Send Image",Type.IMAGE_SENT) )
               adapter?.notifyItemInserted(insertIndex)
           }
           R.id.receive_video ->  {
               val insertIndex = 0
               messagesList.add(insertIndex,Message("Receive Video",Type.VIDEO_RECEIVED) )
               adapter?.notifyItemInserted(insertIndex)
           }
           R.id.send_office -> {
               val insertIndex = 0
               messagesList.add(insertIndex,Message("Office Sent",Type.OFFICE_SENT) )
               adapter?.notifyItemInserted(insertIndex)
           }

           R.id.send_file -> {
               Log.d(TAG,"User wants to send a file")
               sendFile()
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
            messagesList.add(insertIndex,Message(message,Type.SENT) )
            adapter?.notifyItemInserted(insertIndex)
            messageBody.text.clear()
        }else{
            Toast.makeText(applicationContext,"Client is not connected to the server.Message is not sent",Toast.LENGTH_LONG).show()
        }
    }

    private fun recieveMessgesAndUpdateChatView(from:String,body:String){
        if (from==contactJID){
           Log.d(TAG,"New message recieved from $contactJID")
            Log.d(TAG,"Message Body $body")


            val insertIndex = 0
            messagesList.add(insertIndex,Message(body, Type.RECEIVED) )
            adapter?.notifyItemInserted(insertIndex)

        }else{
            Log.d(TAG,"Got a message from another jid : $contactJID")
        }

    }


    private fun sendFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, FILE_SELECT_ACTIVITY_REQUEST_CODE)
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

                    NetworkConnectionService
                }
            }
        }
    }
}

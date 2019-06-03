package com.example.smackandroid.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.example.smackandroid.modal.Contact
import com.example.smackandroid.R
import com.example.smackandroid.service.NetworkConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smackx.iqregister.AccountManager
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Localpart
import java.lang.Exception





class ContactListActivity : AppCompatActivity() {


    private val TAG="ContactListActivity"

    lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private var contactList= arrayListOf<Contact>()
    private val SmackAndroidRequestExternalStorage=11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        recyclerView=findViewById(R.id.contact_list_recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(this)
        addContacts()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter= ContactAdapter(contactList, goToChatActivity)
        recyclerView.adapter=adapter

        askStoragePermissions()


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode==SmackAndroidRequestExternalStorage){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(this, "Storage Permissions Granted for Smack Android", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "You won't be able to send or receive messages without granting permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addContacts():ArrayList<Contact>{
        contactList.add(Contact("chathuminav@wellness.hsenidmobile.com"))
        contactList.add(Contact("sithumk@wellness.hsenidmobile.com"))
        contactList.add(Contact("abc@wellness.hsenidmobile.com"))
        contactList.add(Contact("def@wellness.hsenidmobile.com"))
        contactList.add(Contact("user5@server.com"))
        return contactList
    }


    private val goToChatActivity:(Int)->Unit={
        addContactToRoster(contactList[it].jid)
        //registerUser()
        val intent=Intent(this, ChatActivity::class.java)
        intent.putExtra("jid",contactList[it].jid)
        startActivity(intent)
    }

    private fun registerUser(){
        try {
            Log.d(TAG,"Creating a new user")

            val accountManager=AccountManager.getInstance(NetworkConnection.mConnection)

            if (accountManager.supportsAccountCreation()){
                val attributes = HashMap<String,String>()



                accountManager.sensitiveOperationOverInsecureConnection(true)
                accountManager.createAccount(Localpart.from("amazinguser") ,"test123#")
            }



        }catch (e:Exception){
           e.printStackTrace()
        }

    }


    private fun addContactToRoster(jid:String){
//          if (NetworkConnection.roster!!.isLoaded){
//              Log.d(TAG,"Roster is loaded")
//              try {
//                  NetworkConnection.roster!!.reloadAndWait()
//              } catch (e: SmackException.NotLoggedInException) {
//                  Log.i(TAG, "NotLoggedInException")
//                  e.printStackTrace()
//              } catch (e: SmackException.NotConnectedException) {
//                  Log.i(TAG, "NotConnectedException")
//                  e.printStackTrace()
//              }
//
//          }
//
//          val rosterEntries=NetworkConnection.roster!!.entries
//          rosterEntries.forEach {
//              Log.e(TAG,"Available items in the roster : ${it.jid}")
//          }
//
//         NetworkConnection.roster!!.createEntry(JidCreate.bareFrom(jid),"abc",null)
        val subscribe = Presence(Presence.Type.subscribe)
        subscribe.to=JidCreate.from(jid)
        NetworkConnection.mConnection?.sendStanza(subscribe)

        val available=Presence(Presence.Type.available,"I am available",42, Presence.Mode.chat)
        NetworkConnection.mConnection?.sendStanza(available)
    }



    private fun askStoragePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),SmackAndroidRequestExternalStorage
                )
            }
        }
    }
}

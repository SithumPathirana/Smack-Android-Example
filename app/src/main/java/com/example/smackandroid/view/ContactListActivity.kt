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
import android.widget.Toast
import com.example.smackandroid.modal.Contact
import com.example.smackandroid.R

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
        contactList.add(Contact("chathuminav@xmpp.si"))
        contactList.add(Contact("sithumk@xmpp.si"))
        contactList.add(Contact("user3@server.com"))
        contactList.add(Contact("user4@server.com"))
        contactList.add(Contact("user5@server.com"))
        return contactList
    }


    private val goToChatActivity:(Int)->Unit={
        val intent=Intent(this, ChatActivity::class.java)
        intent.putExtra("jid",contactList[it].jid)
        startActivity(intent)
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

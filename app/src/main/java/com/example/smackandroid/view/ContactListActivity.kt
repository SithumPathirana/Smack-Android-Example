package com.example.smackandroid.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.smackandroid.modal.Contact
import com.example.smackandroid.R

class ContactListActivity : AppCompatActivity() {


    private val TAG="ContactListActivity"

    lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private var contactList= arrayListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)

        recyclerView=findViewById(R.id.contact_list_recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(this)
        addContacts()
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter= ContactAdapter(contactList, goToChatActivity)
        recyclerView.adapter=adapter


    }

    private fun addContacts():ArrayList<Contact>{
        contactList.add(Contact("user1@server.com"))
        contactList.add(Contact("user2@server.com"))
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
}

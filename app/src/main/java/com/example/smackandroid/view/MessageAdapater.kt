package com.example.smackandroid.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smackandroid.R
import com.example.smackandroid.modal.Message

class MessageAdapater(private val messageList:ArrayList<Message>):RecyclerView.Adapter<MessgeVeiwHolder>(){

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(p0: MessgeVeiwHolder, p1: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MessgeVeiwHolder {
        return MessgeVeiwHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false))
    }
}

class MessgeVeiwHolder(view: View):RecyclerView.ViewHolder(view){

}
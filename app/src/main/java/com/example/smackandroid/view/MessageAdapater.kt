package com.example.smackandroid.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.smackandroid.R
import com.example.smackandroid.modal.Message




class MessageAdapater(private val messageList:ArrayList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun getItemViewType(position: Int): Int {

      return  if (messageList[position].sentByUser){
             1
        }else{
             0
        }


    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
              1 -> bindMyMessageViewHolder(holder,position)
              0 -> bindTheirMessageViewHolder(holder,position)
        }
    }


    private fun bindMyMessageViewHolder(holder: RecyclerView.ViewHolder,position: Int){
         val a= holder as MyMessgeVeiwHolder
         a.myMessage?.text=messageList[position].text
    }

    private fun bindTheirMessageViewHolder(holder: RecyclerView.ViewHolder,position: Int){
        val a= holder as TheirMessageViewHolder
        a.theirMessage?.text=messageList[position].text
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {

        var viewHolder:RecyclerView.ViewHolder?=null

        when(viewType){
            1 ->  viewHolder= MyMessgeVeiwHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_message, parent, false))
            0 ->  viewHolder =TheirMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.their_message, parent, false))
        }
         return viewHolder!!
    }
}

class MyMessgeVeiwHolder(view: View):RecyclerView.ViewHolder(view){
     var myMessage:TextView?=null

    init {
        myMessage=view.findViewById(R.id.my_message_body)

    }
}


class TheirMessageViewHolder(view: View):RecyclerView.ViewHolder(view){
    var theirMessage:TextView?=null

    init {
        theirMessage=view.findViewById(R.id.their_message_body)
    }
}


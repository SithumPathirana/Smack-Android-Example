package com.example.smackandroid.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.example.smackandroid.modal.Contact
import com.example.smackandroid.R


class ContactAdapter(private val list:ArrayList<Contact>, private val itemClickListner:(Int)->Unit):RecyclerView.Adapter<ContactViewHolder>(){

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact =list[position]
        holder.jid?.text =contact.jid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_contact, parent, false)
        return ContactViewHolder(view).listen{ position, type ->
           itemClickListner(position)
        }
    }

    fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }

}

class ContactViewHolder(view: View):RecyclerView.ViewHolder(view){

    var jid:TextView?=null

    init {
        jid=view.findViewById(R.id.contact_jid)
    }

}
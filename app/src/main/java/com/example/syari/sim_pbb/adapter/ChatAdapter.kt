package com.example.syari.sim_pbb.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.syari.sim_pbb.model.Chat
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.chat_reply.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*

class ChatAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataSet = arrayListOf<Chat>()
    private val USER = 0
    private val ANOTHER_USER = 1

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            USER -> viewHolder = ViewHolder1(
                LayoutInflater.from(p0.context).inflate(
                    R.layout.chat_sender,
                    p0,
                    false
                )
            )
            ANOTHER_USER -> viewHolder =
                    ViewHolder2(
                        LayoutInflater.from(p0.context).inflate(
                            R.layout.chat_reply,
                            p0,
                            false
                        )
                    )
        }

        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            USER -> {
                val viewHolder = holder as ViewHolder1
                configureViewHolder1(viewHolder, position)
            }
            ANOTHER_USER -> {
                val viewHolder = holder as ViewHolder2
                configureViewHolder2(viewHolder, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (dataSet.get(position).username.equals("admin", true)) {
            return ANOTHER_USER
        } else {
            return USER
        }
    }

    private fun configureViewHolder1(holder: ViewHolder1, position: Int) {

        holder.itemView.text_sender.text = dataSet.get(position).msg
        holder.itemView.text_sender_time.text = dataSet.get(position).time
    }

    private fun configureViewHolder2(holder: ViewHolder2, position: Int) {
        holder.itemView.text_reply.text = dataSet.get(position).msg
        holder.itemView.text_reply_time.text = dataSet.get(position).time
        holder.itemView.text_reply_name.text = dataSet.get(position).username
    }

    class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView)
    class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView)

}
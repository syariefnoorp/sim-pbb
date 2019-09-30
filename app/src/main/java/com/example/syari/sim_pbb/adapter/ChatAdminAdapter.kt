package com.example.syari.sim_pbb.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.syari.sim_pbb.R


/**
 * Created by bett on 8/21/17.
 */
class ChatAdminAdapter(private var activity: Activity, private var listRoom: ArrayList<String>,private var chatMasuk : ArrayList<String>): BaseAdapter() {

    private class ViewHolder(row: View?) {
        var txtName: TextView? = null
        var badge: TextView? = null

        init {
            this.txtName = row?.findViewById<TextView>(R.id.badgeUsername)
            this.badge = row?.findViewById<TextView>(R.id.badgeNotifAdmin)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.layout_room_chat, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var username = listRoom[position]
        viewHolder.txtName?.text = username

        for (i in 0..listRoom.size-1){
            for (j in 0..chatMasuk.size-1){
                if (listRoom[i].equals(chatMasuk[j],true)){
                    Log.d("chat masuk",chatMasuk[j])
                    viewHolder.badge!!.visibility = View.VISIBLE
                }
            }
        }
        notifyDataSetChanged()

        return view as View
    }

    override fun getItem(i: Int): Any {
        return listRoom[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return listRoom.size
    }


}
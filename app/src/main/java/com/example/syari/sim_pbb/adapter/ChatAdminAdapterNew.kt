package com.example.syari.sim_pbb.adapter

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.syari.sim_pbb.menu_activity.ChatRoomActivity
import com.example.syari.sim_pbb.R
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.layout_room_chat.view.*

class ChatAdminAdapterNew(private var activity: Activity,private var username : String, private var listRoom: ArrayList<String>, private var chatMasuk : ArrayList<String>): RecyclerView.Adapter<ChatAdminAdapterNew.ViewHolder>() {

    private var list_chat_dilihat_admin = ArrayList<String>()
    private var list_chat_masuk = ArrayList<String>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        //list_chat_dilihat_admin.clear()
        Rak.initialize(activity)
        list_chat_dilihat_admin = Rak.grab("list_chat_dilihat_admin",ArrayList())


        val view = LayoutInflater.from(p0.context).inflate(R.layout.layout_room_chat,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listRoom.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list_chat_masuk = Rak.grab("list_chat_masuk_admin",ArrayList())
        println("list chat masuk di adapter "+list_chat_masuk)
        println("size list chat masuk di adapter : "+list_chat_masuk.size)

        holder.itemView.badgeUsername.text = listRoom[position]
        holder.itemView.btnSeeChat.setOnClickListener({
//            list_chat_dilihat_admin.clear()
//            Rak.remove("list_chat_dilihat_admin")
            holder.itemView.badgeNotifAdmin.visibility = View.INVISIBLE


            for (i in 0..list_chat_masuk.size-1){
                if (listRoom[position].equals(list_chat_masuk[i],true)){
                    list_chat_masuk.removeAt(i)
                    Rak.entry("list_chat_masuk_admin",list_chat_masuk)
                    println("list chat masuk di adapter updated"+list_chat_masuk)
                    break
                }
            }



            println("list chat dilihat admin "+list_chat_dilihat_admin)
            //println("save list chat dilihat admin "+Rak.grab("list_chat_dilihat_admin"))

//            var status = false
//            for (i in 0..list_chat_dilihat_admin.size-1){
//                if (list_chat_dilihat_admin[i].equals(listRoom[position],true)){
//                    status = true
//                }
//            }
//
//
//
//            if (!status){
//                list_chat_dilihat_admin.add(listRoom[position])
//                Rak.entry("list_chat_dilihat_admin",list_chat_dilihat_admin)
//                println("tambah list chat dilihat admin : "+list_chat_dilihat_admin)
//            }

            val intent = Intent(activity, ChatRoomActivity::class.java)
            intent.putExtra("room_name", holder.itemView.badgeUsername.text.toString())
            intent.putExtra("username",username)
            activity.startActivity(intent)
        })

        for (i in 0..listRoom.size-1){
            for (j in 0..chatMasuk.size-1){
                if (listRoom[position].equals(chatMasuk[j],true)){
                    Log.d("chat masuk",chatMasuk[j])
                    holder.itemView.badgeNotifAdmin.visibility = View.VISIBLE


//                    var sudahDilihatAdmin = false
//                    for (k in 0..list_chat_dilihat_admin.size-1){
//                        if (listRoom[position].equals(list_chat_dilihat_admin[k],true)){
//                            sudahDilihatAdmin = true
//                        }
//                    }
//
//                    if (!sudahDilihatAdmin){
//                        Log.d("chat masuk",chatMasuk[j])
//                        holder.itemView.badgeNotifAdmin.visibility = View.VISIBLE
//                    }
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
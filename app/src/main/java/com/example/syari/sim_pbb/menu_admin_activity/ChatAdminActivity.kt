package com.example.syari.sim_pbb.menu_admin_activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.example.syari.sim_pbb.*
import com.example.syari.sim_pbb.adapter.ChatAdminAdapterNew
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.activity_chat_admin.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatAdminActivity : AppCompatActivity() {

    private lateinit var username : String
    private val root = FirebaseDatabase.getInstance().getReference().root
    private lateinit var arrayAdapter : ArrayAdapter<String>
    private var list_chat_user = HashMap<String,Int>()
    private var list_chat_user_sebelum = HashMap<String,Int>()
    private var list_of_rooms = ArrayList<String>()
    private var urutan_rooms = ArrayList<String>()
    private var list_chat_masuk = ArrayList<String>()
    private var list_chat_masuk_sebelum = ArrayList<String>()
    private lateinit var adapter : ChatAdminAdapterNew
    private lateinit var rv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_admin)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "List Pengaduan"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        rv = listChat

        Rak.initialize(this)
        //Rak.remove("list_chat_user")
        list_chat_masuk = Rak.grab("list_chat_masuk_admin",ArrayList<String>())
        list_chat_user_sebelum = Rak.grab("list_chat_user", HashMap())
        println("list chat user sebelum "+list_chat_user_sebelum)
        println("list chat masuk admin "+list_chat_masuk)

        val sharedPref2 = getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
        username = sharedPref2.getString("username","default")

        root.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val set = HashSet<String>()
                val i = p0.children.iterator()
                while (i.hasNext()){
                    set.add(i.next().key!!)
                }
                list_of_rooms.clear()
                list_of_rooms.addAll(set)
            }
        })

        getChat()


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                this.finish()
            }
        }

        return true
    }

    private fun getChat() {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                println("run chat admin")
                var counter = 0
                var name = "admin"
                //list_chat_masuk.clear()
                for (data in p0.children) {
                    for (dataChild in data.children) {
                        //var chatId = dataChild.ref.parent?.key
                        for (i in 0..list_of_rooms.size-1){
                            if (dataChild.child("username").value.toString().equals(list_of_rooms[i], true)) {

                                name = list_of_rooms[i]
                                counter++

                            }
                        }
                    }
                    list_chat_user.put(name,counter)
                    counter = 0
                }

                println("list chat user size : "+list_chat_user.size)
                println("map : "+list_chat_user)

                var add = false
                for (i in 0..list_chat_user.size-1){
                    if (list_chat_user.get(list_of_rooms[i])!=list_chat_user_sebelum.get(list_of_rooms[i])){
                        println("chat yang baru masuk : "+list_of_rooms[i])
                        if (list_chat_masuk.size>0&&!list_chat_masuk[list_chat_masuk.size-1].equals(list_of_rooms[i],true)){
                            list_chat_masuk.remove(list_of_rooms[i])
                        }
                        var status = false
                        for (j in 0..list_chat_masuk.size-1){
                            if (list_of_rooms[i].equals(list_chat_masuk[j],true)){
                                status = true
                            }
                        }
                        if (!status || list_chat_user.size==0){
                            list_chat_masuk.add(list_of_rooms[i])
                            println("tambahhhh")
                            add = true
                        }
                    }
                }
                if (add){
                    Rak.entry("list_chat_user",list_chat_user)
                }

                Rak.entry("list_chat_masuk_admin",list_chat_masuk)
                println("tambah list chat masuk : "+list_chat_masuk)
                println("list room "+list_of_rooms)
                urutan_rooms = Rak.grab("urutan_rooms",ArrayList())
                println("urutan sebelum"+urutan_rooms)
                if (list_chat_masuk.size>0 ){

                    urutan_rooms.clear()
                    println("urutan diclear")
                    for (index in list_chat_masuk.size-1 downTo 0){
                        urutan_rooms.add(list_chat_masuk[index])
                    }
                }

                var statusUrutan = false
                for (index in 0..list_of_rooms.size-1){
                    statusUrutan = false
                    for (id in 0..urutan_rooms.size-1){
                        if (list_of_rooms[index].equals(urutan_rooms[id],true)){
                            statusUrutan = true
                            break
                        }
                    }
                    println("status urutan "+statusUrutan)
                    if (!statusUrutan){
                        urutan_rooms.add(list_of_rooms[index])
                        println("nambah urutan "+list_of_rooms[index])
                    }

                }
                println("urutan sesudah"+urutan_rooms)
                Rak.entry("urutan_rooms",urutan_rooms)
                if (list_chat_user.size==0){
                    urutan_rooms.clear()
                    Rak.remove("urutan_rooms")
                }

                adapter = ChatAdminAdapterNew(
                    this@ChatAdminActivity,
                    username,
                    urutan_rooms,
                    list_chat_masuk
                )
                rv.layoutManager = LinearLayoutManager(this@ChatAdminActivity, LinearLayout.VERTICAL, false)
                rv.adapter = adapter
                //rv.addItemDecoration(DividerItemDecoration(this@ChatAdminActivity, LinearLayout.HORIZONTAL))
                //adapter.notifyDataSetChanged()

            }
        })

    }

}

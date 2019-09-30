package com.example.syari.sim_pbb.menu_activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.example.syari.sim_pbb.R
import com.example.syari.sim_pbb.adapter.ChatAdapter
import com.example.syari.sim_pbb.model.Chat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var username : String
    private lateinit var room_name : String
    private lateinit var identity : String
    private lateinit var root : DatabaseReference
    private lateinit var inputMsg : String
    private lateinit var temp_key : String
    private val dataSet = ArrayList<Chat>()
    private lateinit var adapter : ChatAdapter
    private lateinit var rv : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        username = intent.getStringExtra("username").toString()
        room_name = intent.getStringExtra("room_name").toString()
        rv = recyclerview_chat

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = room_name
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        if (username.equals("admin",true)){
            identity = room_name
        }else{
            identity = username
        }

        root = FirebaseDatabase.getInstance().reference.child(identity)
        getChat()

        btnSend.setOnClickListener({
            val map = HashMap<String, Any>()
            temp_key = root.push().key!!
            root.updateChildren(map)

            val formatter = SimpleDateFormat("HH:mm")
            val date = Date()
            val sent = formatter.format(date)

            val message_root = root.child(temp_key)
            val chatObj = HashMap<String,Any>()
            inputMsg = editText.text.toString()
            chatObj.put("username",username)
            chatObj.put("msg",inputMsg)
            chatObj.put("time",sent)
            message_root.updateChildren(chatObj)

            editText.setText("")

        })

        root.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                getChat()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                getChat()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun getChat(){
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                dataSet.clear()
                for (data in p0.children){
                    for (dataChild in data.children){
                        var chatId = dataChild.ref.parent?.key
                        println("error chat : "+dataChild.value+" id chat : "+chatId)
                        val chat = dataChild.getValue(Chat::class.java)
                        if (chatId.equals(identity,true)){
                            dataSet.add(chat!!)
                        }
                    }
                }
                adapter = ChatAdapter(this@ChatRoomActivity)
                adapter.dataSet = dataSet
                rv.layoutManager = LinearLayoutManager(this@ChatRoomActivity,LinearLayoutManager.VERTICAL,false)
                rv.adapter = adapter
                if (adapter.itemCount>0){
                    rv.smoothScrollToPosition(adapter.itemCount-1)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                this.finish()
            }
        }

        return true
    }
}

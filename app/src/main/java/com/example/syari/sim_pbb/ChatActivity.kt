package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.syari.sim_pbb.menu_activity.ChatRoomActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.HashSet

class ChatActivity : AppCompatActivity() {

    private val root = FirebaseDatabase.getInstance().getReference().root
    private lateinit var username : String
    private lateinit var nip : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Pengaduan"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        val sharedPref = getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
        username = sharedPref.getString("username","default")
        nip = sharedPref.getString("nip","-")

        infoUsername.text = "Username : "+username

        btnStartChat.setOnClickListener({
            cekChat(username)

            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("room_name","Pengaduan")
            intent.putExtra("username",username)
            startActivity(intent)
        })

        root.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val set = HashSet<String>()
                val i = p0.children.iterator()
                while (i.hasNext()){
                    set.add(i.next().key!!)
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

    private fun cekChat(cekUsername : String){
        root.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot : DataSnapshot) {
                var status = false

                for (data in dataSnapshot.children){
                    if (cekUsername.equals(data.ref.key,false)){
                        status = true
                    }
                }

                if (status==false){
                    val map = HashMap<String,Any>()
                    map.put(cekUsername, "")
                    root.updateChildren(map)
                }
            }
        })
    }
}

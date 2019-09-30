package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.syari.sim_pbb.menu_admin_activity.BeritaAdminActivity
import com.example.syari.sim_pbb.menu_admin_activity.ChatAdminActivity
import com.example.syari.sim_pbb.menu_admin_activity.ConfigActivity
import com.example.syari.sim_pbb.menu_admin_activity.FaqAdminActivity
import com.example.syari.sim_pbb.model.Chat
import com.google.firebase.database.*
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.activity_dashboard_admin.*
import java.util.HashSet

class DashboardAdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var username : String
    private lateinit var nip : String

    private var dataChat : ArrayList<Chat> = ArrayList()
    private var list_chat_masuk = ArrayList<String>()
    private var list_of_rooms = ArrayList<String>()
    private var list_chat_user = HashMap<String,Int>()
    private var list_chat_user_sebelum = HashMap<String,Int>()
    private var chatDibaca: Int = 0
    private var chatDisplay: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var badgeChatAdmin: TextView
    private lateinit var root: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        Rak.initialize(this)

//        username = intent.getStringExtra("nm_login")
//        nip = intent.getStringExtra("nip")
        username = "ADMIN"
        nip = "lkasdjlasd"

        badgeChatAdmin = badgeCounterChat
        sharedPref = getSharedPreferences("SAVE_LAST_VALUE", Context.MODE_PRIVATE)
        chatDibaca = sharedPref.getInt("last_chat_" + username.toLowerCase(), 0)
        chatDisplay = sharedPref.getInt("display_chat_" + username.toLowerCase(), 0)
        list_chat_masuk = Rak.grab("list_chat_masuk_admin",ArrayList<String>())
        list_chat_user_sebelum = Rak.grab("list_chat_user", HashMap())


        println("save chat admin : "+chatDibaca)

        val sharedPref2 = getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
        val editor = sharedPref2.edit()
        editor.putString("username",username)
        editor.putString("nip",nip)
        editor.apply()

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setHomeButtonEnabled(true)

        cv_chat.setOnClickListener({
            val intent = Intent(this, ChatAdminActivity::class.java)

            //badgeChatAdmin.visibility = View.GONE
            chatDibaca = dataChat.size
            println("chat dibuka : " + chatDibaca)

            val editor = sharedPref.edit()
            editor.putInt("last_chat_" + username.toLowerCase(), chatDibaca)
            editor.apply()

            //updateBadge()

            startActivity(intent)
        })
        cv_news.setOnClickListener({
            val intent = Intent(this, BeritaAdminActivity::class.java)

            startActivity(intent)
        })
        cv_faq.setOnClickListener({
            val intent = Intent(this, FaqAdminActivity::class.java)

            startActivity(intent)
        })
        cv_config.setOnClickListener({
            val intent = Intent(this, ConfigActivity::class.java)

            startActivity(intent)
        })

        btn_keluar.setOnClickListener({

            this.finish()
        })

        root = FirebaseDatabase.getInstance().getReference().root

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

                //arrayAdapter.notifyDataSetChanged()
            }

        })

        getChat()

        //change item nav drawer username
        nav_view.menu.getItem(0).subMenu.getItem(0).title = username
        nav_view.menu.getItem(0).subMenu.getItem(1).title = nip

        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{

        }
        Toast.makeText(this, "Please Log Out Your Account", Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()

        list_chat_masuk = Rak.grab("list_chat_masuk_admin",ArrayList<String>())
        list_chat_user_sebelum = Rak.grab("list_chat_user", HashMap())

        getChat()

        println("list chat masuk sebelum "+list_chat_user_sebelum)
        println("list chat masuk dashboard di restart"+list_chat_masuk)
        println("list room "+list_of_rooms)
        updateBadge()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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

                println("list chat masuk sebelum "+list_chat_user_sebelum)
                println("list chat masuk dashboard"+list_chat_masuk)
                println("list room "+list_of_rooms)
                updateBadge()

//                println("tambah list chat masuk : "+list_chat_masuk)
//                println("list room "+list_of_rooms)

            }
        })

    }

    private fun updateBadge() {
        if (list_chat_masuk.size>0){
            badgeChatAdmin.visibility = View.VISIBLE
        }else if(list_chat_masuk.size==0){
            badgeChatAdmin.visibility = View.GONE
        }
    }
}

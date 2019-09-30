package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.syari.sim_pbb.menu_activity.*
import com.example.syari.sim_pbb.model.Chat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.fixedRateTimer

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var username: String
    private lateinit var nip: String
    private var beritaList: ArrayList<HashMap<String, String>> = ArrayList()
    private var beritaDibaca = 0
    private var beritaDisplay = 0
    private lateinit var badgeBerita: TextView
    private lateinit var badgeChat: TextView
    private var beritaDibuka = false
    private lateinit var loading: LinearLayout
    private lateinit var btnBerita: LinearLayout
    private lateinit var root: DatabaseReference
    private val root2 = FirebaseDatabase.getInstance().getReference().root
    private var dataSet = java.util.ArrayList<Chat>()
    private var chatDibaca: Int = 0
    private var chatDisplay: Int = 0
    private lateinit var sharedPref: SharedPreferences

    private lateinit var content1: LinearLayout
    private lateinit var judul1: TextView
    private lateinit var tgl1: TextView
    private lateinit var isi1: TextView

    private lateinit var content2: LinearLayout
    private lateinit var judul2: TextView
    private lateinit var tgl2: TextView
    private lateinit var isi2: TextView

    private lateinit var content3: LinearLayout
    private lateinit var judul3: TextView
    private lateinit var tgl3: TextView
    private lateinit var isi3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        badgeChat = badgeCounterChat
        badgeBerita = badgeCounterBerita
        loading = loadingGuest
        btnBerita = lihatSemuaBerita

        content1 = contentBerita1
        judul1 = judulBerita1
        tgl1 = tglBerita1
        isi1 = isiBerita1

        content2 = contentBerita2
        judul2 = judulBerita2
        tgl2 = tglBerita2
        isi2 = isiBerita2

        content3 = contentBerita3
        judul3 = judulBerita3
        tgl3 = tglBerita3
        isi3 = isiBerita3

        GetBerita().execute()

        username = intent.getStringExtra("nm_login")
        nip = intent.getStringExtra("nip")
//        username = "sarip"
//        nip = "lkasdjlasd"

        sharedPref = getSharedPreferences("SAVE_LAST_VALUE", Context.MODE_PRIVATE)
        chatDibaca = sharedPref.getInt("last_chat_" + username, 0)
        chatDisplay = sharedPref.getInt("display_chat_" + username, 0)
        beritaDibaca = sharedPref.getInt("last_berita_" + username, 0)
        beritaDisplay = sharedPref.getInt("display_berita_"+username,0)
        //sharedPref.edit().remove("last_chat_" + username).commit()

        //updateBadge()
        println("save berita : " + beritaDibaca)
        println("save chat : " + chatDibaca)

        val sharedPref2 = getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
        val editor = sharedPref2.edit()
        editor.putString("username", username)
        editor.putString("nip", nip)
        editor.apply()

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setHomeButtonEnabled(true)

        cv_cekNop.setOnClickListener({
            val intent = Intent(this, CekNopActivity::class.java)

            startActivity(intent)
        })
        cv_catPembayaran.setOnClickListener({
            val intent = Intent(this, CatatanPembayaranActivity::class.java)

            startActivity(intent)
        })
        cv_chat.setOnClickListener({
//            val intent = Intent(this, ChatActivity::class.java)
//            badgeChat.visibility = View.GONE
//            chatDibaca = dataSet.size
//            println("chat dibuka : " + chatDibaca)
//
//            val editor = sharedPref.edit()
//            editor.putInt("last_chat_" + username, chatDibaca)
//            editor.apply()
//
//            updateBadge()
//
//            startActivity(intent)

            cekChat(username)

            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("room_name","Pengaduan")
            intent.putExtra("username",username)
            badgeChat.visibility = View.GONE
            chatDibaca = dataSet.size
            println("chat dibuka : " + chatDibaca)

            val editor = sharedPref.edit()
            editor.putInt("last_chat_" + username, chatDibaca)
            editor.apply()

            updateBadge()
            startActivity(intent)
        })
        cv_news.setOnClickListener({
            val intent = Intent(this, BeritaActivity::class.java)
            badgeBerita.visibility = View.GONE
            beritaDibuka = true
            beritaDibaca = beritaList.size

            val editor = sharedPref.edit()
            editor.putInt("last_berita_" + username, beritaDibaca)
            editor.apply()

            updateBadgeBerita()

            startActivity(intent)
        })
        cv_faq.setOnClickListener({
            val intent = Intent(this, FaqActivity::class.java)

            startActivity(intent)
        })

        btn_keluar.setOnClickListener({
            this.finish()
        })
        btn_berita.setOnClickListener({
            val intent = Intent(this, BeritaActivity::class.java)

            startActivity(intent)
        })
        content1.setOnClickListener({
            val intent = Intent(this, DetailBeritaActivity::class.java)
            intent.putExtra("judul_berita", beritaList[0].get("judul").toString())
            intent.putExtra("tanggal_berita", beritaList[0].get("tanggal").toString())
            intent.putExtra("isi_berita", beritaList[0].get("isi").toString())
            startActivity(intent)
        })
        content2.setOnClickListener({
            val intent = Intent(this, DetailBeritaActivity::class.java)
            intent.putExtra("judul_berita", beritaList[1].get("judul").toString())
            intent.putExtra("tanggal_berita", beritaList[1].get("tanggal").toString())
            intent.putExtra("isi_berita", beritaList[1].get("isi").toString())
            startActivity(intent)
        })
        content3.setOnClickListener({
            val intent = Intent(this, DetailBeritaActivity::class.java)
            intent.putExtra("judul_berita", beritaList[2].get("judul").toString())
            intent.putExtra("tanggal_berita", beritaList[2].get("tanggal").toString())
            intent.putExtra("isi_berita", beritaList[2].get("isi").toString())
            startActivity(intent)
        })

        root = FirebaseDatabase.getInstance().reference.child(username)

        root.addChildEventListener(object : ChildEventListener {
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

        root2.addValueEventListener(object : ValueEventListener{
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

        //change item nav drawer username
        nav_view.menu.getItem(0).subMenu.getItem(0).title = username
        nav_view.menu.getItem(0).subMenu.getItem(1).title = nip

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()

        fixedRateTimer("timer", false, 0L, 5 * 60 * 1000) {
            GetBerita().execute()
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {

        }
        Toast.makeText(this, "Please Log Out Your Account", Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    inner class GetBerita : AsyncTask<Void, Void, Void>() {
        var beritaBaru = false
        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)
            println("update size : "+beritaList.size)
            if (beritaList.size > 0) {
                println("update berita")
                judul1.text = beritaList[0].get("judul").toString()
                tgl1.text = beritaList[0].get("tanggal").toString()
                isi1.text = reduceString(beritaList[0].get("isi").toString())

                judul2.text = beritaList[1].get("judul").toString()
                tgl2.text = beritaList[1].get("tanggal").toString()
                isi2.text = reduceString(beritaList[1].get("isi").toString())

                judul3.text = beritaList[2].get("judul").toString()
                tgl3.text = beritaList[2].get("tanggal").toString()
                isi3.text = reduceString(beritaList[2].get("isi").toString())

                content1.visibility = View.VISIBLE
                content2.visibility = View.VISIBLE
                content3.visibility = View.VISIBLE
                btnBerita.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE

            if (beritaDibaca < beritaList.size) {
                updateBadgeBerita()
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@DashboardActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api + "/select_berita.php")
            Log.e("Respon Berita", "Response berita dashboard : " + jsonStr)

            if (jsonStr != null) {
                println("berita tidak null")
                println("dibaca : "+beritaDibaca)
                try {
                    var beritas = JSONArray(jsonStr)
                    if (true) {
                        beritaList = ArrayList()
                        for (i in 0..beritas.length()) {
                            var c = beritas.getJSONObject(i) as JSONObject

                            var judul = c.getString("NEWS_TITLE")
                            var tanggal = c.getString("NEWS_DATE")
                            var isi = c.getString("NEWS_CONTENT")

                            var berita: HashMap<String, String> = HashMap()

                            berita.put("judul", judul)
                            berita.put("tanggal", tanggal)
                            berita.put("isi", isi)

                            beritaList.add(berita)
                            beritaBaru = true
                        }
                    }

                } catch (e: JSONException) {

                }
            }
            println("add size berita : "+beritaList.size)
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }

    private fun reduceString(teks: String): String {
        var hasil = ""
        if (teks.length>25){
            val isi = teks.substring(0..25)
            hasil = isi + "..."
        }else{
            hasil = teks
        }

        return hasil
    }

    private fun getChat() {
        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                dataSet.clear()
                for (data in p0.children) {
                    for (dataChild in data.children) {
                        var chatId = dataChild.ref.parent?.key
                        if (chatId.equals(username, true) &&
                            dataChild.child("username").value.toString().equals("ADMIN", true)
                        ) {
                            val chat = dataChild.getValue(Chat::class.java)
                            dataSet.add(chat!!)

                        }
                    }
                }

                if (dataSet.size > chatDibaca) {
                    updateBadge()
                }
            }
        })

    }

    private fun updateBadge() {
        var displayBadge = dataSet.size - chatDibaca

        val editor = sharedPref.edit()
        editor.putInt("display_chat_" + username, displayBadge)
        editor.apply()

        if (displayBadge != 0) {
            badgeChat.text = "" + displayBadge
            badgeChat.visibility = View.VISIBLE
        }

        println("jumlah chat sebelum : " + chatDibaca)
        println("jumlah chat : " + dataSet.size)
    }

    private fun updateBadgeBerita(){
        var displayBadge = beritaList.size - beritaDibaca

        val editor = sharedPref.edit()
        editor.putInt("display_berita_" + username, displayBadge)
        editor.apply()

        if (displayBadge != 0) {
            badgeBerita.text = "" + displayBadge
            badgeBerita.visibility = View.VISIBLE
        }

        println("jumlah berita dibaca : " + beritaDibaca)
        println("jumlah berita : " + beritaList.size)
    }

    private fun cekChat(cekUsername : String){
        root2.addListenerForSingleValueEvent(object : ValueEventListener{
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
                    val map = java.util.HashMap<String, Any>()
                    map.put(cekUsername, "")
                    root2.updateChildren(map)
                }
            }
        })
    }
}

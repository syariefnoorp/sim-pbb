package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.syari.sim_pbb.menu_activity.*
import kotlinx.android.synthetic.main.activity_dashboard_guest.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.concurrent.fixedRateTimer

class DashboardGuestActivity : AppCompatActivity() {

    private lateinit var drawer : DrawerLayout
    private lateinit var toggle : ActionBarDrawerToggle
    private var beritaList:ArrayList<HashMap<String,String>> = ArrayList()
    private var jmlBerita = 0
    private lateinit var badge : TextView
    private var beritaDibuka = false
    private lateinit var loading : LinearLayout
    private lateinit var btnBerita : LinearLayout
    private lateinit var listBerita : ListView

    private lateinit var content1 : LinearLayout
    private lateinit var judul1 : TextView
    private lateinit var tgl1 : TextView
    private lateinit var isi1 : TextView

    private lateinit var content2 : LinearLayout
    private lateinit var judul2 : TextView
    private lateinit var tgl2 : TextView
    private lateinit var isi2 : TextView

    private lateinit var content3 : LinearLayout
    private lateinit var judul3 : TextView
    private lateinit var tgl3 : TextView
    private lateinit var isi3 : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_guest)

        badge = badgeCounter
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

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
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
        cv_news.setOnClickListener({
            val intent = Intent(this, BeritaActivity::class.java)
            badge.visibility = View.GONE
            beritaDibuka = true
            startActivity(intent)
        })
        cv_faq.setOnClickListener({
            val intent = Intent(this, FaqActivity::class.java)

            startActivity(intent)
        })

        btn_loginGuest.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
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

    }

    override fun onResume() {
        super.onResume()

        fixedRateTimer("timer",false,0L,10*60*1000){
            GetBerita().execute()
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    inner class GetBerita : AsyncTask<Void, Void, Void>() {
        var dataBaru = false
        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)
            println("update size guest : "+beritaList.size)
            if (beritaList.size>0){

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

            if (jmlBerita<beritaList.size && dataBaru==true){
                if (beritaDibuka){
                    beritaDibuka = false
                    jmlBerita = beritaList.size
                }
                var displayBadge = beritaList.size - jmlBerita
                if (displayBadge!=0){
                    badge.text = ""
                    badge.visibility = View.VISIBLE
                }

                dataBaru = false

                println("jumlah berita : "+jmlBerita)
                println("jumlah list : "+beritaList.size)
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@DashboardGuestActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_berita.php")
            Log.e("Respon JSON", "Response from url : " + jsonStr)

            if (jsonStr != null) {

                try {
                    var beritas = JSONArray(jsonStr)
                    if (true){
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
                            dataBaru = true
                        }
                    }

                } catch (e: JSONException) {

                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }

    private fun reduceString(teks : String) : String{
        var hasil = ""
        if (teks.length>25){
            val isi = teks.substring(0..25)
            hasil = isi + "..."
        }else{
            hasil = teks
        }

        return hasil
    }
}

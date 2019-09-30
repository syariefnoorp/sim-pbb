package com.example.syari.sim_pbb.menu_activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_berita.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BeritaActivity : AppCompatActivity() {

    private var beritaList:ArrayList<HashMap<String,String>> = ArrayList()
    private lateinit var loading : RelativeLayout
    private lateinit var layout : ListView
    private lateinit var teks_error : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berita)

        loading = loadingBerita
        layout = listBerita
        teks_error = teksKosong

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Berita"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        GetBerita().execute()

        val detailBerita = Intent(this,DetailBeritaActivity::class.java)
        listBerita.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = listBerita.getItemAtPosition(position) as HashMap<String,String>
                println("hashmap : ${item.get("judul")}")

                detailBerita.putExtra("judul_berita",item.get("judul"))
                detailBerita.putExtra("tanggal_berita",item.get("tanggal"))
                detailBerita.putExtra("isi_berita",item.get("isi"))

                startActivity(detailBerita)
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                this.finish()
            }
        }

        return true
    }

    inner class GetBerita : AsyncTask<Void, Void, Void>() {

        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)

            if (beritaList.size>0){
                var adapter = SimpleAdapter(
                    this@BeritaActivity, beritaList,
                    R.layout.list_berita, arrayOf("judul", "tanggal", "isi"),
                    intArrayOf(R.id.judulBerita, R.id.tglBerita, R.id.isiBerita)
                )
                listBerita.adapter = adapter

                layout.visibility = View.VISIBLE
            }else{
                teks_error.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@BeritaActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_berita.php")
            Log.e("Respon JSON", "Response from url : " + jsonStr)

            if (jsonStr != null) {
                try {
                    var beritas = JSONArray(jsonStr)
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
                    }
                } catch (e: JSONException) {

                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            //loading.visibility = View.VISIBLE
        }
    }
}

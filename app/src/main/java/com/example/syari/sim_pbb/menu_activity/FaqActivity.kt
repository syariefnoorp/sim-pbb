package com.example.syari.sim_pbb.menu_activity

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import com.example.syari.sim_pbb.adapter.FaqAdapter
import kotlinx.android.synthetic.main.activity_faq.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FaqActivity : AppCompatActivity() {

    var kategoriData : HashMap<String,ArrayList<HashMap<String, String>>> = hashMapOf()
    private lateinit var loading : LinearLayout
    private lateinit var list : RecyclerView
    private lateinit var teks_error : TextView
    private lateinit var adapter: FaqAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        loading = loadingFaq
        list = listKategori
        teks_error = teksKosong

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        GetFaq().execute()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> {
                this.finish()
            }
        }

        return true
    }

    inner class GetFaq : AsyncTask<Void, Void, Void>() {

        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)

            if (kategoriData.size>0){

                adapter = FaqAdapter(this@FaqActivity, kategoriData)
                listKategori.layoutManager = LinearLayoutManager(this@FaqActivity, LinearLayout.VERTICAL, false)
                listKategori.adapter = adapter

            }else{
                teks_error.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@FaqActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonFaq = sh.makeServiceCall(api+"/select_faq.php")
            Log.e("Respon JSON", "Response from url : " + jsonFaq)

            var kategoriSebelum : ArrayList<String> = ArrayList()

            if (jsonFaq != null) {
                try {
                    var faqs = JSONArray(jsonFaq)
                    for (i in 0..faqs.length()) {
                        var c = faqs.getJSONObject(i) as JSONObject

                        var tanya = c.getString("FAQ_TITLE")
                        var jawab = c.getString("FAQ_CONTENT")
                        var kategori = c.getString("FAQ_CATEGORY")

                        var faq: HashMap<String, String> = HashMap()

                        faq.put("tanya", tanya)
                        faq.put("jawab", jawab)

                        var validasiKategori = true

                        for (i in 0..kategoriSebelum.size-1){
                            if (kategori.equals(kategoriSebelum[i])){
                                validasiKategori = false
                            }
                        }

                        if (validasiKategori){
                            kategoriData.put(kategori.toString(),arrayListOf())
                        }

                        kategoriData[kategori.toString()]?.add(faq)
                        println("size : "+kategoriData.size)
                        //faqList.add(faq)
                        kategoriSebelum.add(kategori)

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
}

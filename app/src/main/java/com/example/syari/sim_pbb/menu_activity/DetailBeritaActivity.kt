package com.example.syari.sim_pbb.menu_activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_detail_berita.*

class DetailBeritaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_berita)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Berita"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        val judul = intent.getStringExtra("judul_berita")
        val tanggal = intent.getStringExtra("tanggal_berita")
        val isi = intent.getStringExtra("isi_berita")

        detailJudul.text = judul
        detailTgl.text = tanggal
        detailIsi.text = isi
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

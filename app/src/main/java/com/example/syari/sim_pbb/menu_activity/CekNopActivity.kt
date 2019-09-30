package com.example.syari.sim_pbb.menu_activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.example.syari.sim_pbb.adapter.PageAdapter
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_cek_nop.*

class CekNopActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var pageAdapter: PageAdapter
    private lateinit var username : String
    private lateinit var nip : String
    private lateinit var nop : String
    private lateinit var tahun : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cek_nop)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Cek NOP Tunggal"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        btnGoCekNop.setOnClickListener({
            val intent = Intent(this, DetailCekNopActivity::class.java)
            nop = inputNop.text.toString()
            tahun = tahunNop.text.toString()
            intent.putExtra("nop",nop)
            intent.putExtra("tahun",tahun)

            startActivity(intent)
        })
        btnResetCekNop.setOnClickListener({
            inputNop.text.clear()
            tahunNop.text.clear()
        })

        //set fragments
//        pageAdapter = PageAdapter(supportFragmentManager)
//        pageAdapter!!.addFragment(CekNopFragment())
//        pageAdapter!!.addFragment(CatatanFragment())
//        pageAdapter!!.addFragment(PengaduanFragment())
//        pageAdapter!!.addFragment(BeritaFragment())
//        container.adapter = pageAdapter
//
//        tabs.setupWithViewPager(container)

        //set icon fragment
//        for (i in 0..tabs.tabCount){
//            when(i){
//                0 -> {
//                    tabs.getTabAt(i)?.setIcon(R.drawable.ceknop_white)
//                }
//                1 -> {
//                    tabs.getTabAt(i)?.setIcon(R.drawable.printer_white)
//                }
//                2 -> {
//                    tabs.getTabAt(i)?.setIcon(R.drawable.chat_white)
//                }
//                3 -> {
//                    tabs.getTabAt(i)?.setIcon(R.drawable.news_white)
//                }
//            }
//        }

        //get shared preferences
        val sharedPref = getSharedPreferences("USERNAME",Context.MODE_PRIVATE)
        username = sharedPref.getString("username","default")
        nip = sharedPref.getString("nip","-")

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

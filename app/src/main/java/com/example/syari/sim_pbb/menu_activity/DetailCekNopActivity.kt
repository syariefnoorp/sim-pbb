package com.example.syari.sim_pbb.menu_activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.syari.sim_pbb.adapter.PageContentAdapter
import com.example.syari.sim_pbb.R
import com.example.syari.sim_pbb.fragments_content_cekNop.DataPajakFragment
import com.example.syari.sim_pbb.fragments_content_cekNop.PbbFragment
import kotlinx.android.synthetic.main.activity_detail_cek_nop.*

class DetailCekNopActivity : AppCompatActivity() {

    private lateinit var pageContentAdapter: PageContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_cek_nop)

        //set action bar
        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Detail Data Pajak"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        pageContentAdapter = PageContentAdapter(supportFragmentManager)
        pageContentAdapter!!.addFragment(DataPajakFragment(),"Data Wajib Pajak")
        pageContentAdapter!!.addFragment(PbbFragment(),"PBB")
        content.adapter = pageContentAdapter
        tabContent.setupWithViewPager(content)

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

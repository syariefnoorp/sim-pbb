package com.example.syari.sim_pbb.menu_admin_activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_setting_api.*

class SettingApiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_api)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Setting API"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        val sharedPref = this@SettingApiActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
        val api = sharedPref?.getString("api", "default")

        linkApi.setText(api.toString())

        btnResetApi.setOnClickListener({
            linkApi.text.clear()
        })
        btnUpdateApi.setOnClickListener({

            val editor = sharedPref.edit()
            editor.putString("api",linkApi.text.toString())
            editor.commit()

            Toast.makeText(this,"Update API Berhasil",Toast.LENGTH_LONG).show()
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

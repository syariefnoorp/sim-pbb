package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class SplashScreenActivity : AppCompatActivity() {

    var usersList: ArrayList<HashMap<String, String>> = ArrayList()
    private lateinit var ngrok : String
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this,DashboardGuestActivity::class.java)
            sharedPref = getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("api","http://202.57.31.54:5152/simpbb")
            editor.apply()

            startActivity(intent)
            this.finish()
        },3000)
        Rak.initialize(applicationContext)

    }

}

package com.example.syari.sim_pbb

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mindorks.editdrawabletext.DrawablePosition
import com.mindorks.editdrawabletext.onDrawableClickListener
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    var usersList:ArrayList<HashMap<String,String>> = ArrayList()
    private lateinit var text : TextView
    private lateinit var loading : ProgressBar
    private lateinit var btn : Button
    private lateinit var api : String
    private lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Rak.initialize(applicationContext)

        sharedPref = this@MainActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
        api = sharedPref?.getString("api", "default")

        text = textWait
        loading = loadingLogin
        btn = btnLogin

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = "Login"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        Glide.with(this)
            .load(api+"/uploads/logo.png")
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.logo_egov_putih)
            .into(logo)

        btnLogin.setOnClickListener({
            GetUsers().execute()
//
//            val intent = Intent(this,DashboardActivity::class.java)
//            startActivity(intent)
        })
        btnLoginAdmin.setOnClickListener({
            val intent = Intent(this,DashboardAdminActivity::class.java)
            startActivity(intent)
        })

        //set on click drawable
        var visibility = true
        password.setDrawableClickListener(object : onDrawableClickListener{
            override fun onClick(target: DrawablePosition) {

                if (target == DrawablePosition.RIGHT){
                    if (!visibility) {
                        password.transformationMethod = PasswordTransformationMethod.getInstance()
                        visibility = true
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logo_pass,0,R.drawable.ic_invisible_pass,0)
                        password.setSelection(password.text.length)
                    } else {
                        password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                        visibility = false
                        password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.logo_pass,0,R.drawable.ic_visibility_pass,0)
                        password.setSelection(password.text.length)
                    }
                }
            }
        })

    }

    private fun encrypt_md5Hash(str:String):String{
        var m : MessageDigest? = null
        try {
            m = MessageDigest.getInstance("MD5")
        }catch (e:NoSuchAlgorithmException){
            e.printStackTrace()
        }

        m?.update(str.toByteArray(),0,str.length)
        var hash = BigInteger(1,m?.digest()).toString(16)

        return hash
    }

    private fun login(username:String,password:String){
        var status = false
        var nip = "-"
        for (i in 0..usersList.size-1){

            if (usersList.get(i).get("nm_login").equals(username)&&
                (usersList.get(i).get("password").equals(encrypt_md5Hash(password))||
                        usersList.get(i).get("password").equals(password))){

                status = true
                nip = usersList.get(i).get("nip").toString()
            }
        }
        println("size : "+usersList.size)
        if (status==false){
            Toast.makeText(this@MainActivity,"Username atau Password Salah!", Toast.LENGTH_SHORT).show()
        }else{
            var intent : Intent
            if (username.equals("ADMIN")){
                intent = Intent(this,DashboardAdminActivity::class.java)
            }else{
                intent = Intent(this,DashboardActivity::class.java)
            }
            Toast.makeText(this@MainActivity,"Login Sukses", Toast.LENGTH_SHORT).show()
            intent.putExtra("nm_login",username)
            intent.putExtra("nip",nip)
            startActivity(intent)
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

    inner class GetUsers : AsyncTask<Void, Void, Void>() {
        var statusData = false
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (statusData){
                btn.visibility = View.VISIBLE
                login(username.text.toString(),password.text.toString())
            }
            text.visibility = View.GONE
            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_user.php")
            Log.e("Respon JSON","Response from url : "+jsonStr)

            if(jsonStr!=null){
                try {
                    var users = JSONArray(jsonStr)
                    for (i in 0..users.length()){
                        var c = users.getJSONObject(i) as JSONObject

                        var nm_login = c.getString("NM_LOGIN")
                        var password = c.getString("PASSWORD")
                        var nip = c.getString("NIP")

                        var user:HashMap<String, String> = HashMap()

                        user.put("nm_login", nm_login)
                        user.put("password", password)
                        user.put("nip",nip)

                        usersList.add(user)

                        statusData = true
                    }
                }catch (e: JSONException){

                }
            }else{

                this@MainActivity.runOnUiThread {
                    Toast.makeText(this@MainActivity,"Koneksi Offline", Toast.LENGTH_LONG).show()
                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            text.visibility = View.VISIBLE
            loading.visibility = View.VISIBLE
        }
    }
}

package com.example.syari.sim_pbb.menu_admin_activity

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.Toast
import com.example.syari.sim_pbb.DashboardAdminActivity
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_berita_admin.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import android.view.WindowManager
import android.view.ViewTreeObserver
import io.isfaaghyth.rak.Rak
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class BeritaAdminActivity : AppCompatActivity() {

    private lateinit var Judul : EditText
    private lateinit var Berita : EditText
    private lateinit var loading : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berita_admin)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Input Berita"
        supportActionBar?.setBackgroundDrawable(getDrawable(R.drawable.bg_nav_menu))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        Rak.initialize(this)
        val heightLayout = Rak.grab("height_layout",0)
        val param1 = btnAsli.layoutParams
        val param2 = btnPalsu.layoutParams

        if (heightLayout!=0){
            param1.height = heightLayout
            param2.height = heightLayout
        }

        btnPalsu.visibility = View.GONE

        val rootWindow = window
        scrollBerita.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                var screenHeight = scrollBerita.rootView.height
                var r = Rect()
                val view = rootWindow.decorView
                view.getWindowVisibleDisplayFrame(r)

                var keyboardHeight = screenHeight - r.bottom
                var toDp = (keyboardHeight/this@BeritaAdminActivity.resources.displayMetrics.density).toInt()
                //setHeight = toDp+20

                println("keyboard height : "+toDp)

                KeyboardVisibilityEvent.setEventListener(this@BeritaAdminActivity, object : KeyboardVisibilityEventListener{
                    override fun onVisibilityChanged(isOpen: Boolean) {
                        if (isOpen){
                            btnPalsu.visibility = View.INVISIBLE
                        }else if (!isOpen){
                            btnPalsu.visibility = View.GONE
                        }
                    }
                })

                if (toDp>0){

                    var setHeight = toDp
                    println("set height : "+setHeight)
                    val param1 = btnAsli.layoutParams
                    val param2 = btnPalsu.layoutParams

                    Rak.entry("height_layout",setHeight*2)

                    param1.height = setHeight*2
                    param2.height = setHeight*2
                }

            }
        })

        Berita = inputBerita
        Judul = inputJudul
        loading = loadingSubmitBerita

        btnSubmitBerita.setOnClickListener({
            if (Judul.text.toString().equals("") || Berita.text.toString().equals("")){
                Toast.makeText(this@BeritaAdminActivity,"Harap lengkapi semua data", Toast.LENGTH_LONG).show()
            }else{
                AddBerita().execute()
            }
        })
        btnCancelBerita.setOnClickListener({
            this.finish()
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

    inner class AddBerita : AsyncTask<Void, Void, Void>() {
        var status = true
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (status){
                Toast.makeText(this@BeritaAdminActivity,"Input Berita Berhasil", Toast.LENGTH_LONG).show()
                Judul.text.clear()
                Berita.text.clear()

                Judul.clearFocus()
                Berita.clearFocus()
            }else{
                Toast.makeText(this@BeritaAdminActivity,"Input Berita Gagal, Periksa Koneksi Anda!", Toast.LENGTH_LONG).show()
            }

            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@BeritaAdminActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var judul = Judul.text.toString()
            var berita = Berita.text.toString()

            val sh = HttpHandler()
            val returnUrl = sh.makeServiceCall(api+"/insert_berita.php?judul="+URLEncoder.encode(judul,"UTF-8")+"&isi="+URLEncoder.encode(berita,"UTF-8"))
            println("returnUrl : "+returnUrl)
            if (returnUrl==null){
                status = false

            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            loading.visibility = View.VISIBLE
        }
    }

}

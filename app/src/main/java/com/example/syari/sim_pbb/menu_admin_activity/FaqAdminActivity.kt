package com.example.syari.sim_pbb.menu_admin_activity

import android.content.Context
import android.graphics.Rect
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import io.isfaaghyth.rak.Rak
import kotlinx.android.synthetic.main.activity_faq_admin.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.net.URLEncoder

class FaqAdminActivity : AppCompatActivity() {

    private lateinit var pertanyaan : EditText
    private lateinit var jawaban : EditText
    private lateinit var kategori : EditText
    private lateinit var loading : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_admin)

        pertanyaan = inputPertanyaan
        jawaban = inputJawaban
        kategori = inputKategori
        loading = loadingSubmitFaq

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Input FAQ"
        supportActionBar?.setBackgroundDrawable(getDrawable(R.drawable.bg_nav_menu))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        Rak.initialize(this)
        val heightLayout = Rak.grab("height_faq_layout",0)
        val param1 = btnAsli.layoutParams
        val param2 = btnPalsu.layoutParams

        if (heightLayout!=0){
            param1.height = heightLayout - 100
            param2.height = heightLayout
        }

        btnPalsu.visibility = View.GONE

        val rootWindow = window
        scrollFaq.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                var screenHeight = scrollFaq.rootView.height
                var r = Rect()
                val view = rootWindow.decorView
                view.getWindowVisibleDisplayFrame(r)

                var keyboardHeight = screenHeight - r.bottom
                var toDp = (keyboardHeight/this@FaqAdminActivity.resources.displayMetrics.density).toInt()
                //setHeight = toDp+20

                println("keyboard height faq : "+toDp)

                KeyboardVisibilityEvent.setEventListener(this@FaqAdminActivity, object : KeyboardVisibilityEventListener {
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
                    println("set height faq : "+setHeight)
                    var param1 = btnAsli.layoutParams
                    var param2 = btnPalsu.layoutParams

                    Rak.entry("height_faq_layout",setHeight*2)

                    param1.height = setHeight*2  - 100
                    param2.height = setHeight*2
                }

            }
        })

        btnCancelFaq.setOnClickListener({
            this.finish()
        })
        btnSubmitFaq.setOnClickListener({
            if (pertanyaan.text.toString().equals("")||jawaban.text.toString().equals("")||kategori.text.toString().equals("")){
                Toast.makeText(this@FaqAdminActivity,"Harap lengkapi semua data", Toast.LENGTH_LONG).show()
            }else{
                AddFaq().execute()
            }
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

    inner class AddFaq : AsyncTask<Void, Void, Void>() {
        var status = true
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (status){
                Toast.makeText(this@FaqAdminActivity,"Input FAQ Berhasil", Toast.LENGTH_LONG).show()
                pertanyaan.text.clear()
                kategori.text.clear()
                jawaban.text.clear()

                pertanyaan.clearFocus()
                kategori.clearFocus()
                jawaban.clearFocus()
            }else{
                Toast.makeText(this@FaqAdminActivity,"Input FAQ Gagal, Periksa Koneksi Anda", Toast.LENGTH_LONG).show()
            }

            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@FaqAdminActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var ask = pertanyaan.text.toString()
            var answer = jawaban.text.toString()
            var kat = kategori.text.toString()

            val sh = HttpHandler()
            val returnUrl = sh.makeServiceCall(api+"/insert_faq.php?tanya="+
                    URLEncoder.encode(ask,"UTF-8")+"&jawab="+
                    URLEncoder.encode(answer,"UTF-8")+"&kategori="+
                    URLEncoder.encode(kat,"UTF-8"))
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

package com.example.syari.sim_pbb.menu_admin_activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_ganti_logo.*
import net.gotev.uploadservice.MultipartUploadRequest
import net.gotev.uploadservice.UploadNotificationConfig
import org.json.JSONException
import java.util.*

class GantiLogoActivity : AppCompatActivity() {

    private lateinit var filepath: Uri
    private lateinit var info_file: TextView
    private lateinit var loading: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var logoText: EditText
    private var link = "link gambar"
    private val PICK_IMG_REQUEST = 71

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ganti_logo)

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Ganti Logo"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        info_file = file_path
        loading = loadingUpload

        btnUpload.setOnClickListener({
            if (info_file.text.equals("Klik Gambar Untuk Upload!")) {
                Toast.makeText(this, "Silahkan klik gambar untuk memilih file.", Toast.LENGTH_SHORT).show()
            } else {
                StartUpload().execute()
            }

        })
        addImg.setOnClickListener({
            pilihGambar()
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

    private fun pilihGambar() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "pilih gambar"), PICK_IMG_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filepath = data.data
            if (filepath != null) {
                info_file.text = "file siap untuk diupload. klik submit!"
            }
        }
    }

    private fun uploadImg() {
        val name = "gambar"

        val path = filepath.path

        try {
            Toast.makeText(this, "Uploading Logo....", Toast.LENGTH_LONG).show()
            val uploadId = UUID.randomUUID().toString()

            val sharedPref = this@GantiLogoActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            MultipartUploadRequest(this, uploadId, api + "/upload_img.php?api="+api)
                .addFileToUpload(path, "image")
                .addParameter("name", name)
                .setNotificationConfig(UploadNotificationConfig())
                .setMaxRetries(2)
                .startUpload()

        } catch (exc: Exception) {
            Toast.makeText(this, exc.message, Toast.LENGTH_LONG).show()
        }
    }

    inner class StartUpload : AsyncTask<Void, Void, Void>() {
        var respon = false
        override fun onPostExecute(result: Void?) {

            super.onPostExecute(result)

            if (respon){
                uploadImg()
            }else if(!respon){
                Toast.makeText(this@GantiLogoActivity, "Upload Image Gagal, Periksa Koneksi Anda", Toast.LENGTH_LONG).show()
            }

            loading.visibility = View.GONE
            info_file.text = "Klik Gambar Untuk Upload!"

        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@GantiLogoActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var deleteImg = sh.makeServiceCall(api + "/delete_img.php?api="+api)
            Log.e("Respon Delete", "Response from delete img : " + deleteImg)
            println(deleteImg.toString())
            if (deleteImg != null) {
                println("not null")
                try {
                    respon = true
                    println("respon delete : "+deleteImg.toString())

                } catch (e: JSONException) {

                }
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()

            loading.visibility = View.VISIBLE
        }
    }
}

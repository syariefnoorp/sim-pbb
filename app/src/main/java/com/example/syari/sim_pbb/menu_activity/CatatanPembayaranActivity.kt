package com.example.syari.sim_pbb.menu_activity

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.syari.sim_pbb.HttpHandler
import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.activity_catatan_pembayaran.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CatatanPembayaranActivity : AppCompatActivity() {

    private lateinit var username : String
    private lateinit var nip : String

    var dataCpList:ArrayList<HashMap<String,String>> = ArrayList()
    private lateinit var inputNop : String
    private lateinit var tahunView : TextView
    private lateinit var pbbView : TextView
    private lateinit var jatuhTempo : TextView
    private lateinit var jumlahBayarView : TextView
    private lateinit var keView : TextView
    private lateinit var tglBayarView : TextView
    private lateinit var perekamView : TextView
    private lateinit var bankView : TextView

    private lateinit var spinnerTahunView : Spinner
    private lateinit var textError : TextView
    private lateinit var loading : ProgressBar
    private lateinit var textLoading : TextView
    private lateinit var layout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catatan_pembayaran)

        tahunView = tahun
        pbbView = pbb
        jatuhTempo = jatem
        jumlahBayarView = jumlahBayar
        keView = ke
        tglBayarView = tglBayar
        perekamView = perekam
        bankView = bank
        spinnerTahunView = spinnerTahun
        textError = text_error
        loading = loadingDataCp
        textLoading = text_wait
        layout = layout_dataPembayaran

        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        //set action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Catatan Pembayaran"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#006fe3")))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        supportActionBar?.setHomeButtonEnabled(true)

        btnGoCp.setOnClickListener({
            dataCpList = ArrayList()
            inputNop = inputNopCp.text.toString()
            loading.visibility = View.VISIBLE
            textLoading.visibility = View.VISIBLE
            textError.visibility = View.GONE
            layout.visibility = View.GONE
            spinnerTahun.visibility = View.GONE
            GetCatatanPembayaran().execute()
        })
        btnResetCp.setOnClickListener({
            inputNopCp.text.clear()
            layout.visibility = View.GONE
            spinnerTahun.visibility = View.GONE
            textError.visibility = View.GONE
            dataCpList = ArrayList()
        })

        spinnerTahun.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var isiPbb = dataCpList.get(position).get("pbb").toString()
                var isiJumlahBayar = dataCpList.get(position).get("jumlah").toString()

                //create format for 2000 to 2.000,00
                val kurs = DecimalFormat.getCurrencyInstance() as DecimalFormat
                val rp = DecimalFormatSymbols()
                rp.currencySymbol = ""
                rp.monetaryDecimalSeparator = ','
                rp.groupingSeparator = '.'
                kurs.decimalFormatSymbols = rp

                tahun.text = dataCpList.get(position).get("tahun").toString()
                pbb.text = kurs.format(isiPbb.toInt())
                jatuhTempo.text = dataCpList.get(position).get("jatuhTempo").toString()
                jumlahBayar.text = kurs.format(isiJumlahBayar.toInt())
                ke.text = dataCpList.get(position).get("ke").toString()
                tglBayar.text = dataCpList.get(position).get("tglBayar").toString()
                perekam.text = dataCpList.get(position).get("perekam").toString().trim()
                bank.text = dataCpList.get(position).get("bank").toString().trim()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        val sharedPref = getSharedPreferences("USERNAME", Context.MODE_PRIVATE)
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

    inner class GetCatatanPembayaran : AsyncTask<Void, Void, Void>() {
        var statusData = false
        var dataKetemu = false
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            var listTahun : ArrayList<String> = ArrayList()
            if (statusData){
                for (i in 0..dataCpList.size-1){
                    listTahun.add(i,dataCpList.get(i).get("tahun").toString())
                }

                val adapter = ArrayAdapter<String>(this@CatatanPembayaranActivity,R.layout.custom_spinner,listTahun)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spinnerTahun.adapter = adapter

                var isiPbb = dataCpList.get(0).get("pbb").toString()
                var isiJumlahBayar = dataCpList.get(0).get("jumlah").toString()

                //create format for 2000 to 2.000,00
                val kurs = DecimalFormat.getCurrencyInstance() as DecimalFormat
                val rp = DecimalFormatSymbols()
                rp.currencySymbol = ""
                rp.monetaryDecimalSeparator = ','
                rp.groupingSeparator = '.'
                kurs.decimalFormatSymbols = rp

                tahun.text = dataCpList.get(0).get("tahun").toString()
                pbb.text = kurs.format(isiPbb.toInt())
                jatuhTempo.text = dataCpList.get(0).get("jatuhTempo").toString()
                jumlahBayar.text = kurs.format(isiJumlahBayar.toInt())
                ke.text = dataCpList.get(0).get("ke").toString()
                tglBayar.text = dataCpList.get(0).get("tglBayar").toString()
                perekam.text = dataCpList.get(0).get("perekam").toString().trim()
                bank.text = dataCpList.get(0).get("bank").toString().trim()
                dataKetemu = true

            }else{
                textError.visibility = View.VISIBLE
            }

            if (!dataKetemu){
                textError.visibility = View.VISIBLE
            }else{
                layout.visibility = View.VISIBLE
                spinnerTahun.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE
            textLoading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = this@CatatanPembayaranActivity.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_pembayaran.php?nop="+inputNop)
            Log.e("Respon JSON","Response from url : "+jsonStr)

            if(jsonStr!=null){
                try {
                    var dataPembayaran = JSONArray(jsonStr)
                    for (i in 0..dataPembayaran.length()){
                        var cursor = dataPembayaran.getJSONObject(i) as JSONObject

                        //data untuk data wajib pajak
                        var tahun = cursor.getString("THN_PAJAK_SPPT")
                        var pbb = cursor.getString("PBB_YG_HARUS_DIBAYAR_SPPT")
                        var jatuhTempo = cursor.getString("TGL_JATUH_TEMPO_SPPT")
                        var jumlah = cursor.getString("JML_SPPT_YG_DIBAYAR")
                        var ke = cursor.getString("PEMBAYARAN_SPPT_KE")
                        var tglBayar = cursor.getString("TGL_PEMBAYARAN_SPPT")
                        var perekam = cursor.getString("NIP_REKAM_BYR_SPPT")
                        var bank = cursor.getString("KD_TP")

                        var getBank = sh.makeServiceCall(api+"/select_bank.php?bank="+bank)
                        Log.e("Respon JSON","Response from url : "+getBank)
                        if (getBank!=null){
                            var arrBank = JSONArray(getBank)
                            var cursor = arrBank.getJSONObject(0) as JSONObject
                            var namaBank = cursor.getString("NM_TP")

                            var data:HashMap<String, String> = HashMap()

                            data.put("tahun", tahun)
                            data.put("pbb", pbb)
                            data.put("jatuhTempo",jatuhTempo)
                            data.put("jumlah",jumlah)
                            data.put("ke",ke)
                            data.put("tglBayar",tglBayar)
                            data.put("perekam",perekam)
                            data.put("bank",namaBank)

                            dataCpList.add(data)
                            statusData = true
                        }
                    }
                }catch (e: JSONException){

                }
            }else{
                this@CatatanPembayaranActivity?.runOnUiThread {
                    Toast.makeText(this@CatatanPembayaranActivity,"Koneksi Bermasalah, Silahkan Coba Lagi!", Toast.LENGTH_LONG).show()
                }

            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }
}

package com.example.syari.sim_pbb.menu_fragment


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.syari.sim_pbb.HttpHandler

import com.example.syari.sim_pbb.R
import kotlinx.android.synthetic.main.fragment_catatan.*
import kotlinx.android.synthetic.main.fragment_catatan.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CatatanFragment : Fragment() {

    var dataCpList:ArrayList<HashMap<String,String>> = ArrayList()
    private lateinit var nop : String
    private lateinit var tahun : TextView
    private lateinit var pbb : TextView
    private lateinit var jatuhTempo : TextView
    private lateinit var jumlahBayar : TextView
    private lateinit var ke : TextView
    private lateinit var tglBayar : TextView
    private lateinit var perekam : TextView
    private lateinit var bank : TextView

    private lateinit var spinnerTahun : Spinner
    private lateinit var textError : TextView
    private lateinit var loading : ProgressBar
    private lateinit var textLoading : TextView
    private lateinit var layout : LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_catatan, container, false)
//        var adapter = ArrayAdapter<String>(view.context,R.layout.custom_spinner,listTahun)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        tahun = view.tahun
        pbb = view.pbb
        jatuhTempo = view.jatem
        jumlahBayar = view.jumlahBayar
        ke = view.ke
        tglBayar = view.tglBayar
        perekam = view.perekam
        bank = view.bank

        spinnerTahun = view.spinnerTahun
        textError = view.text_error
        loading = view.loadingDataCp
        textLoading = view.text_wait
        layout = view.layout_dataPembayaran

        view.btnGoCp.setOnClickListener({
            nop = view.inputNop.text.toString()
            loading.visibility = View.VISIBLE
            textLoading.visibility = View.VISIBLE
            textError.visibility = View.GONE
            layout.visibility = View.GONE
            spinnerTahun.visibility = View.GONE
            GetCatatanPembayaran().execute()
        })

        view.btnResetCp.setOnClickListener({
            view.inputNop.text.clear()
            layout.visibility = View.GONE
            spinnerTahun.visibility = View.GONE
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

        return view
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

                val adapter = ArrayAdapter<String>(context,R.layout.custom_spinner,listTahun)
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
            val sharedPref = context?.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val ngrok = sharedPref?.getString("ngrok","default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall("http://"+ngrok+".ngrok.io/select_pembayaran.php?nop="+nop)
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


                        var data:HashMap<String, String> = HashMap()

                        data.put("tahun", tahun)
                        data.put("pbb", pbb)
                        data.put("jatuhTempo",jatuhTempo)
                        data.put("jumlah",jumlah)
                        data.put("ke",ke)
                        data.put("tglBayar",tglBayar)
                        data.put("perekam",perekam)
                        data.put("bank",bank)

                        dataCpList.add(data)
                        statusData = true
                    }
                }catch (e: JSONException){

                }
            }else{
                activity?.runOnUiThread {
                    Toast.makeText(activity,"Koneksi Bermasalah, Silahkan Coba Lagi!", Toast.LENGTH_LONG).show()
                }

            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }
    }

    fun convertRupiahToText(rupiah:Int) : String{
        val huruf:Array<String> = arrayOf("","Satu","Dua","Tiga","Empat","Lima","Enam","Tujuh","Delapan","Sebilan","Sepuluh","Sebelas")

        var hasil = ""
        if(rupiah<12)
            hasil=hasil+huruf[rupiah];
        else if(rupiah<20)
            hasil=hasil+convertRupiahToText(rupiah-10)+" Belas";
        else if(rupiah<100)
            hasil=hasil+convertRupiahToText(rupiah/10)+" Puluh "+convertRupiahToText(rupiah%10);
        else if(rupiah<200)
            hasil=hasil+"Seratus "+convertRupiahToText(rupiah-100);
        else if(rupiah<1000)
            hasil=hasil+convertRupiahToText(rupiah/100)+" Ratus "+convertRupiahToText(rupiah%100);
        else if(rupiah<2000)
            hasil=hasil+"Seribu "+convertRupiahToText(rupiah-1000);
        else if(rupiah<1000000)
            hasil=hasil+convertRupiahToText(rupiah/1000)+" Ribu "+convertRupiahToText(rupiah%1000);
        else if(rupiah<1000000000)
            hasil=hasil+convertRupiahToText(rupiah/1000000)+" Juta "+convertRupiahToText(rupiah%1000000);

        return hasil
    }
}

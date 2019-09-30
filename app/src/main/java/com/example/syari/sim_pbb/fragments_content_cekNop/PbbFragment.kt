package com.example.syari.sim_pbb.fragments_content_cekNop


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
import kotlinx.android.synthetic.main.fragment_pbb.view.*
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
class PbbFragment : Fragment() {

    var dataPbbList:ArrayList<HashMap<String,String>> = ArrayList()
    private var nop : String? = "nop"
    private var tahun : String? = "tahun"

    private lateinit var loading : ProgressBar
    private lateinit var layout : LinearLayout
    private lateinit var error : TextView

    private lateinit var pbbTerhutang : TextView
    private lateinit var pengurang : TextView
    private lateinit var pbbSudahDibayar : TextView
    private lateinit var pbbHarusDibayar : TextView
    private lateinit var denda : TextView
    private lateinit var jatuhTempo : TextView
    private lateinit var terbilang : TextView
    private lateinit var tagihan : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pbb, container, false)

        nop = activity?.intent?.getStringExtra("nop")
        tahun = activity?.intent?.getStringExtra("tahun")

        GetDataPbb().execute()

        loading = view.loadingPbb
        layout = view.layout_dataPbb
        error = view.textError

        pbbTerhutang = view.pbbUtang
        pengurang = view.pengurang
        pbbSudahDibayar = view.pbbSudahBayar
        pbbHarusDibayar = view.pbbHarusBayar
        denda = view.denda
        jatuhTempo = view.jatuhTempo
        terbilang = view.terbilang
        tagihan = view.tagihan

        return view
    }

    inner class GetDataPbb : AsyncTask<Void, Void, Void>() {
        var statusData = false
        var dataKetemu = false
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            if (statusData){
                for (i in 0..dataPbbList.size-1){

                    if (dataPbbList!=null){
                        val terhutangPbb = dataPbbList.get(i).get("pbbTerhutang").toString()
                        val pengurangPbb = dataPbbList.get(i).get("pengurang").toString()
                        val sudahDibayarPbb = dataPbbList.get(i).get("jumlahSppt").toString()
                        val harusDibayarPbb = dataPbbList.get(i).get("pbbHarusDibayar").toString()
                        val dendaAdministrasi = dataPbbList.get(i).get("denda").toString()
                        val statusPembayaran = dataPbbList.get(i).get("statusPembayaran").toString()

                        //create format for 2000 to 2.000,00
                        val kurs = DecimalFormat.getCurrencyInstance() as DecimalFormat
                        val rp = DecimalFormatSymbols()
                        rp.currencySymbol = ""
                        rp.monetaryDecimalSeparator = ','
                        rp.groupingSeparator = '.'
                        kurs.decimalFormatSymbols = rp

                        pbbTerhutang.text = kurs.format(terhutangPbb.toInt())
                        pengurang.text = kurs.format(pengurangPbb.toInt())
                        if (statusPembayaran.equals("1")){
                            pbbSudahDibayar.text = kurs.format(sudahDibayarPbb.toInt())
                        }else{
                            pbbSudahDibayar.text = "0,00"
                        }
                        //pbbSudahDibayar.text = kurs.format(sudahDibayarPbb.toInt())
                        pbbHarusDibayar.text = kurs.format(harusDibayarPbb.toInt())
                        denda.text = kurs.format(dendaAdministrasi.toInt())
                        jatuhTempo.text = dataPbbList.get(i).get("jatuhTempo").toString()
                        terbilang.text = convertRupiahToText(terhutangPbb.toInt())+" Rupiah"
                        tagihan.text = kurs.format(harusDibayarPbb.toInt())

                        dataKetemu = true
                    }
                }

            }else{
                //Toast.makeText(activity,"Can't Connect Web Service, Check Your Connection!", Toast.LENGTH_LONG).show()
                error.text = "Data Tidak Ditemukan"
                error.visibility = View.VISIBLE
            }

            if (!dataKetemu){
                error.visibility = View.VISIBLE
            }else{
                layout.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = context?.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_pbb.php?nop="+nop+"&tahun="+tahun)
            var jsonDenda = sh.makeServiceCall(api+"/select_denda.php?nop="+nop+"&tahun="+tahun)
            Log.e("Respon JSON","Response from url : "+jsonStr)
            Log.e("Denda JSON","Result Denda : "+jsonDenda)

            if(jsonStr!=null){
                try {
                    var dataPajak = JSONArray(jsonStr)
                    var denda = "0"
                    if (jsonDenda!=null){
                        var dendaObj = JSONObject(jsonDenda)
                        denda = dendaObj.getString("DENDA")
                    }

                    var statusBayar = dataPajak.getJSONObject(0).getString("STATUS_PEMBAYARAN_SPPT").toString()
                    if (statusBayar.equals("0")){
                        var cursor = dataPajak.getJSONObject(0) as JSONObject
                        //data untuk PBB
                        var pbbTerhutang = cursor.getString("PBB_TERHUTANG_SPPT")
                        var pengurang = cursor.getString("FAKTOR_PENGURANG_SPPT")
                        var pbbHarusDibayar = cursor.getString("PBB_YG_HARUS_DIBAYAR_SPPT")
                        var jatuhTempo = cursor.getString("TGL_JATUH_TEMPO_SPPT")
                        var statusPembayaran = cursor.getString("STATUS_PEMBAYARAN_SPPT")

                        var data:HashMap<String, String> = HashMap()

                        data.put("pbbTerhutang",pbbTerhutang)
                        data.put("pengurang",pengurang)
                        data.put("pbbHarusDibayar",pbbHarusDibayar)
                        data.put("statusPembayaran",statusPembayaran)
                        data.put("jatuhTempo",jatuhTempo)
                        data.put("denda",denda)

                        dataPbbList.add(data)
                        statusData = true
                    }else{
                        for (i in 0..dataPajak.length()){
                            var cursor = dataPajak.getJSONObject(i) as JSONObject
                            //data untuk PBB
                            var pbbTerhutang = cursor.getString("PBB_TERHUTANG_SPPT")
                            var pengurang = cursor.getString("FAKTOR_PENGURANG_SPPT")
                            var jumlahSppt = cursor.getString("JML_SPPT_YG_DIBAYAR")
                            var pbbHarusDibayar = cursor.getString("PBB_YG_HARUS_DIBAYAR_SPPT")
                            var jatuhTempo = cursor.getString("TGL_JATUH_TEMPO_SPPT")
                            var statusPembayaran = cursor.getString("STATUS_PEMBAYARAN_SPPT")

                            var data:HashMap<String, String> = HashMap()

                            data.put("pbbTerhutang",pbbTerhutang)
                            data.put("pengurang",pengurang)
                            data.put("jumlahSppt",jumlahSppt)
                            data.put("pbbHarusDibayar",pbbHarusDibayar)
                            data.put("jatuhTempo",jatuhTempo)
                            data.put("statusPembayaran",statusPembayaran)
                            data.put("denda",denda)

                            dataPbbList.add(data)
                            statusData = true
                        }
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

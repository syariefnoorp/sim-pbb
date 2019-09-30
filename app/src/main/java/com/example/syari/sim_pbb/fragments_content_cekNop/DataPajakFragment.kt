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
import kotlinx.android.synthetic.main.fragment_data_pajak.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DataPajakFragment : Fragment() {

    var dataPajakList:ArrayList<HashMap<String,String>> = ArrayList()
    var nop : String? = "nop"
    var tahun : String? = "tahun"
    private lateinit var loading : ProgressBar
    private lateinit var layout : LinearLayout
    private lateinit var error : TextView

    private lateinit var nama : TextView
    private lateinit var alamat : TextView
    private lateinit var rtrw : TextView
    private lateinit var kelurahan : TextView
    private lateinit var kota : TextView
    private lateinit var npwp : TextView

    private lateinit var btnFilter : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_data_pajak, container, false)

        GetDataPajak().execute()

        nop = activity?.intent?.getStringExtra("nop")
        tahun = activity?.intent?.getStringExtra("tahun")

        loading = view.loadingDataPajak
        layout = view.layout_dataPajak
        error = view.textError
        nama = view.nama_wp
        alamat = view.alamat_wp
        rtrw = view.rtrw_wp
        kelurahan = view.kelurahan_wp
        kota = view.kota_wp
        npwp = view.npwp_wp
        btnFilter = view.btnResetFilter

        btnFilter.setOnClickListener({
            activity?.finish()
        })

        return view
    }

    inner class GetDataPajak : AsyncTask<Void, Void, Void>() {
        var statusData = false
        var dataKetemu = false
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)


            if (statusData){
                for (i in 0..dataPajakList.size-1){

                    if (dataPajakList.get(i).get("tahun").equals(tahun)){
                        nama.text = dataPajakList.get(i).get("nama").toString()
                        alamat.text = dataPajakList.get(i).get("alamat").toString()
                        rtrw.text = dataPajakList.get(i).get("rw").toString()+"/"+dataPajakList.get(i).get("rt").toString()
                        kelurahan.text = dataPajakList.get(i).get("kelurahan").toString()
                        kota.text = dataPajakList.get(i).get("kota").toString()
                        if (dataPajakList.get(i).get("npwp").toString().equals("null")){
                            npwp.text = "-"
                        }else{
                            npwp.text = dataPajakList.get(i).get("npwp").toString()
                        }
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
                btnFilter.visibility = View.VISIBLE
            }
            loading.visibility = View.GONE
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val sharedPref = context?.getSharedPreferences("PUT_URL", Context.MODE_PRIVATE)
            val api = sharedPref?.getString("api", "default")

            var sh = HttpHandler()
            var jsonStr = sh.makeServiceCall(api+"/select_sppt.php?nop="+nop)
            Log.e("Respon JSON","Response from url : "+jsonStr)

            if(jsonStr!=null){
                try {
                    var dataPajak = JSONArray(jsonStr)
                    for (i in 0..dataPajak.length()){
                        var cursor = dataPajak.getJSONObject(i) as JSONObject

                        //data untuk data wajib pajak
                        var nama = cursor.getString("NM_WP_SPPT")
                        var alamat = cursor.getString("JLN_WP_SPPT")
                        var rw = cursor.getString("RW_WP_SPPT")
                        var rt = cursor.getString("RT_WP_SPPT")
                        var kelurahan = cursor.getString("KELURAHAN_WP_SPPT")
                        var kota = cursor.getString("KOTA_WP_SPPT")
                        var npwp = cursor.getString("NPWP_SPPT")
                        var tahun = cursor.getString("THN_PAJAK_SPPT")

                        //data untuk PBB
                        var pbbTerhutang = cursor.getString("PBB_TERHUTANG_SPPT")
                        var pengurang = cursor.getString("FAKTOR_PENGURANG_SPPT")
                        var pbbHarusDibayar = cursor.getString("PBB_YG_HARUS_DIBAYAR_SPPT")
                        var jatuhTempo = cursor.getString("TGL_JATUH_TEMPO_SPPT")

                        var data:HashMap<String, String> = HashMap()

                        data.put("nama", nama)
                        data.put("alamat", alamat)
                        data.put("rw",rw)
                        data.put("rt",rt)
                        data.put("kelurahan",kelurahan)
                        data.put("kota",kota)
                        data.put("npwp",npwp)
                        data.put("tahun",tahun)
                        data.put("pbbTerhutang",pbbTerhutang)
                        data.put("pengurang",pengurang)
                        data.put("pbbHarusDibayar",pbbHarusDibayar)
                        data.put("jatuhTempo",jatuhTempo)

                        dataPajakList.add(data)
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
}

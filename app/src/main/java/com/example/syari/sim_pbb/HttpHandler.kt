package com.example.syari.sim_pbb

import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

class HttpHandler {
    constructor()

    fun makeServiceCall(reqUrl:String):String? {
        var response:String?=null

        try {

            var url= URL(reqUrl)
            var conn = url.openConnection() as HttpURLConnection
            conn.requestMethod
            var inputStream = BufferedInputStream(conn.inputStream)
            response = convertStreamToString(inputStream)

        } catch (e: MalformedURLException) {
            Log.e("Error handler","MalformedURLException: " + e.message)
        } catch (e: ProtocolException) {
            Log.e("Error handler","ProtocolException: " + e.message)
        } catch (e: IOException) {
            Log.e("Error handler","IOException: " + e.message)
        } catch (e:Exception) {
            Log.e("Error handler","Exception: " + e.message)
        }
        return response
    }

    private fun convertStreamToString(inputStream: InputStream):String{
        var reader = BufferedReader(InputStreamReader(inputStream))
        var sb = StringBuilder()

        var line:String

        try {
            while (true){
                val line = reader.readLine() ?: break
                sb.append(line).append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}
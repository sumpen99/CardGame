package com.example.cardgame.api
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.cardgame.enums.ApiFunction
import com.example.cardgame.enums.HttpResponse
import com.example.cardgame.interfaces.IThreading
import com.example.cardgame.io.*
import com.example.cardgame.json.JsonObject
import com.example.cardgame.methods.getResponseCodeValue
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

class ApiHandler(val context: Context,
                 var args:Array<String>?=null,
                 var apiFunc: ApiFunction?):IThreading {

    private var callbackInProgress:Boolean = false
    private var connectionDisconnected:Boolean = false
    private var MAX_RESPONSE_TIME:Long = 3000
    private lateinit var RESPONSE_CODE:HttpResponse
    private lateinit var httpsCon:HttpsURLConnection
    private var whenFinnished:((args:Any?)->Unit) ? = null
    private var showResponseCode:((args:HttpResponse)->Unit) ? = null

    fun setCallbackFinished(callback:(args:Any?)->Unit){
        whenFinnished = callback
    }

    fun setCallbackResponseCode(callback:(args:HttpResponse)->Unit){
        showResponseCode = callback
    }

    fun checkInternetConnectivity():Boolean{
        val connectivityManager:ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    //printToTerminal("NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    //printToTerminal("NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    //printToTerminal("NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        //printToTerminal("No Internet")
        return false
    }

    fun setApiService(service:ApiFunction){
        apiFunc = service
    }

    fun setArguments(arguments:Array<String>){
        args = arguments
    }

    private fun login():String?{
        val url = getEnv("tokenAuth")
        if(url!=null){
            val userName: String? = getEnv("username")
            val password: String? = getEnv("password")
            val parameters = "username=$userName&password=$password"
            val serverResponse: String = executePostRequest(parameters,url)
            if (serverResponse.isNotEmpty()) {
                val json = JsonObject(serverResponse)
                return json.objMap.getValue("token") as String
            }
        }
        return null
    }

    private fun urlUploadHighScore(){
        val token = login()
        var url: String? = getEnv("uploadNewHighScoreUrl")
        if (token != null && url!=null && args!=null) {
            url += "&name=${args!![0]}&score=${args!![1]}"
            executeGetRequest(url, token)
            /*val serverResponse: String = executeGetRequest(url, token)
            if (serverResponse.isNotEmpty()) {
                val json = JsonObject(serverResponse)

            }*/
        }
    }

    private fun urlGetHighScore() {
        val token = login()
        val url: String? = getEnv("getHighScoreUrl")
        if (token != null && url!=null) {
            val serverResponse: String = executeGetRequest(url, token)
            if (serverResponse.isNotEmpty()) {
                val json = JsonObject(serverResponse)
                val table = json.getHighScoreValues()
                if(table!=null && !connectionDisconnected && whenFinnished!=null){
                    whenFinnished!!(table)
                }
            }
        }
    }

    private fun executePostRequest(parameters: String, url: String): String {
        val postData: ByteArray = parameters.toByteArray(StandardCharsets.UTF_8)
        val postDataLength = postData.size
        var outPutData = ""
        try {
            val urlObject = URL(url)
            val ssls: SSLSocketFactory? = loadSSLCert(context)
            if (ssls != null) {
                httpsCon = urlObject.openConnection() as HttpsURLConnection
                httpsCon.sslSocketFactory = ssls
                httpsCon.doOutput = true
                httpsCon.instanceFollowRedirects = false
                //httpsCon.setDoInput(true);
                httpsCon.requestMethod = "POST"
                //httpsCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpsCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                httpsCon.setRequestProperty("charset", "utf-8")
                httpsCon.setRequestProperty("Content-Length", postDataLength.toString())
                httpsCon.setRequestProperty("Accept", "application/json")
                httpsCon.useCaches = false
                writeOutPutStream(httpsCon, postData)
                outPutData = readInputStream(httpsCon)
                RESPONSE_CODE =  getResponseCodeValue(httpsCon.responseCode)
                httpsCon.disconnect()
                setCallbackStatus(false)
            }
        } catch (e: Exception) {
            printToTerminal(e.message.toString())
        }
        return outPutData
    }

    private fun executeGetRequest(url:String,token:String):String{
        var outPutData = ""
        try{
            val urlObject = URL(url)
            val ssls = loadSSLCert(context)
            if(ssls!=null){
                httpsCon = urlObject.openConnection() as HttpsURLConnection
                httpsCon.sslSocketFactory = ssls
                //httpsCon.doOutput = true
                httpsCon.doInput = true
                httpsCon.requestMethod = "GET"
                httpsCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                httpsCon.setRequestProperty("Accept", "application/json")
                httpsCon.setRequestProperty("Authorization", "Token $token")
                outPutData = readInputStream(httpsCon)
                RESPONSE_CODE =  getResponseCodeValue(httpsCon.responseCode)
                httpsCon.disconnect()
                setCallbackStatus(false)
            }
        }
        catch(err:Exception){
            printToTerminal(err.message.toString())
        }
        return outPutData
    }

    private fun getResponseCode():HttpResponse{
        return RESPONSE_CODE
    }

    fun didConnectionDisconnect():Boolean{
        return connectionDisconnected
    }

    private fun closeConnectionIfNeeded(){
        try{ Thread(Runnable {
            Thread.sleep(MAX_RESPONSE_TIME)
            stopActivity()
        }).start()}
        catch(err:Exception){ printToTerminal(err.message.toString())}
    }

    private fun closeConnection(){
        httpsCon.disconnect()
        setCallbackStatus(false)
    }

    // TODO START TIMER TO DISCONNECT?
    override fun startActivity() {
        closeConnectionIfNeeded()
        when(apiFunc!!){
            ApiFunction.URL_GET_HIGHSCORE->{
                urlGetHighScore()
            }
            ApiFunction.URL_UPLOAD_HIGHSCORE->{
                urlUploadHighScore()
            }
        }
        if(showResponseCode!=null){showResponseCode!!(getResponseCode())}
    }

    override fun stopActivity() {
        if(getCallbackStatus()){
            closeConnection()
            connectionDisconnected = true
        }
    }

    override fun setCallbackStatus(value: Boolean) {
        callbackInProgress = value
    }

    override fun getCallbackStatus():Boolean{
        return callbackInProgress
    }
}
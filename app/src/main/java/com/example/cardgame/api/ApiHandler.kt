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


/*
* Class that runs on a second thread
* Executes Get/Post requests
* Login credentials is needed to access server
* */
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

    /*
    * App apilevel was set to 21 but this function pushed it up to 23
    * We check this one before trying to make https call
    * */
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

    /*
    * login to server to gain token
    * */
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

    /*
    * if successful login we send name and score to server
    * */
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

    /*
    * if successful login we get highscore names/score back
    * */
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

    /*
    * The server uses a selfsigned ssl certificate
    * loadssl handels that
    * */
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

    /*
    * if httpsCon.doOutput is set to true it doesn't matter
    * if we set httpsCon.requestMethod = "GET" it will still make
    * POST request
    * Thanks to the good people at the Internet I found that out
    * Otherwise I would still be looking at this function....
    * */
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

    /*
    * Close the connection if the server has not respond in MAX_RESPONSE_TIME
    * I did not find a built in function for this but maybe their is one?
    * */
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

    /*
    * This function is run by threadhandler onRun
    * */
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
package com.example.cardgame.io
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.example.cardgame.enums.EntrieType
import com.example.cardgame.map.SMHashMap
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell
import java.io.*
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory

var appEnvironmentVariables:SMHashMap?=null

//var appEnvironmentVariables:Map<String,String>?=null


//https://gist.github.com/erickok/7692592
fun loadSSLCert(context:Context):SSLSocketFactory?{
    try {

        // Load CAs from an InputStream
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt

        val pathSSL = getEnv("pathssl")
        //val inputStream: InputStream = FileInputStream(pathSSL)

        val inputStream: InputStream = context.assets.open(pathSSL!!)
        val caInput: InputStream = BufferedInputStream(inputStream)
        val ca: Certificate
        ca = try {
            cf.generateCertificate(caInput)
        } finally {
            caInput.close()
        }

        // Create a KeyStore containing our trusted CAs
        val keyStoreType: String = KeyStore.getDefaultType()
        val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // Create a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // Create an SSLContext that uses our TrustManager
        val context: SSLContext = SSLContext.getInstance("TLS")
        context.init(null, tmf.getTrustManagers(), null)
        return context.getSocketFactory()
    } catch (err: NoSuchAlgorithmException) {
        printToTerminal(err.message.toString())
    } catch (err: KeyStoreException) {
        printToTerminal(err.message.toString())
    } catch (err: KeyManagementException) {
        printToTerminal(err.message.toString())
    } catch (err: CertificateException) {
        printToTerminal(err.message.toString())
    } catch (err: IOException) {
        printToTerminal(err.message.toString())
    }
    return null
}

fun writeOutPutStream(streamOut:HttpsURLConnection,postData:ByteArray){
    val wr = DataOutputStream(streamOut.outputStream)
    try{wr.write(postData)}
    catch(err:java.lang.Exception){printToTerminal(err.message.toString())}
    finally{wr.close()}
}

fun readInputStream(streamIn:HttpsURLConnection):String{
    var line:String?
    var outPutData=""
    val bufferedStreamIn= BufferedReader(InputStreamReader(streamIn.inputStream))
    try{while((bufferedStreamIn.readLine()).also { line=it }!=null){outPutData+=line}}
    catch(err:java.lang.Exception){printToTerminal(err.message.toString())}
    finally{bufferedStreamIn.close()}

    return outPutData
}

fun setAppEnvVariables(context: Context,){
    val envPath = "environment/env"
    val fRead:InputStream?
    val reader:BufferedReader?
    try {
        fRead  = context.assets.open(envPath)
        reader = BufferedReader(InputStreamReader(fRead))
    }
    catch (err:java.lang.Exception){
        printToTerminal(err.message.toString())
        return
    }

    //appEnvironmentVariables = mutableMapOf<String,String>()
    appEnvironmentVariables = SMHashMap(10,.75f)
    var line:String?
    while((reader.readLine()).also{ line = it } != null){
        val key_value = line!!.split(" ")
        appEnvironmentVariables!!.addNewItem(key_value[0],key_value[1], EntrieType.ENTRIE_ENV_VARIABLE)
        //(appEnvironmentVariables as MutableMap<String, String>)[key_value[0]] = key_value[1]
    }
    fRead.close()
    reader.close()
}

fun getEnv(key:String) : String? {
    if(appEnvironmentVariables!=null){
        val envValue = appEnvironmentVariables!!.getValue(key)
        if(envValue!=null){return envValue as String}
    }
    return null
}


fun getPlayingCardBitMap(context: Context, filePath: String) : Bitmap {
    //printToTerminal("$needScaling")
    /*val imagePath = "./assets/cards/clubs_2.png"*/
    val inputStream : InputStream = context.assets.open(filePath)
    var img = BitmapFactory.decodeStream(inputStream)
    if(needScaling){
        val width = getNewWidth()
        img = img.scale(width,(width* getCardRatio()).toInt(),true)
    }
    //printToTerminal("Width: ${img.width} Height: ${img.height}")
    inputStream.close()
    return img

}

fun logScreenDimensions(){
    Log.d("ScreenDimensions","Width: ${getScreenWidth()} Height: ${getScreenHeight()}")
}

fun printGameBoard(gameBoard:Array<BoardCell>){
    var i = 0
    while(i<gameBoard.size){
        printToTerminal("${gameBoard[i].x} ${gameBoard[i].y}")
        i++
    }

}

fun printDeckOfCards(cards:Array<String>?){
    if(cards!=null){printArrayOfStrings(cards!!)}
}

fun printArrayOfStrings(arr:Array<String>){
    var i =0
    while(i<arr.size){printToTerminal(arr[i++])}
}

fun printToTerminal(msg : String){
    Log.d("Message",msg)
}
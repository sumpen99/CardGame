package com.example.cardgame.io
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
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

//https://gist.github.com/erickok/7692592
fun loadSSLCert(context:Context):SSLSocketFactory?{
    try {

        // Load CAs from an InputStream
        val cf: CertificateFactory = CertificateFactory.getInstance("X.509")
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        //val pathSSL = getEnv(context,"pathssl")
        val pathSSL = "ssl/zonfri.crt"
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
    try{
        val wr = DataOutputStream(streamOut.outputStream)
        wr.write(postData)
        wr.close()
    }
    catch(err:java.lang.Exception){
        printToTerminal(err.message.toString())
    }
}

fun readInputStream(streamIn:HttpsURLConnection):String{
    var line:String?
    var outPutData=""
    try{
        val bufferedStreamIn= BufferedReader(InputStreamReader(streamIn.inputStream))
        line = bufferedStreamIn.readLine()
        while(line!=null){
            outPutData+=line
            line=bufferedStreamIn.readLine()
        }
        bufferedStreamIn.close()
    }
    catch(err:java.lang.Exception){
        printToTerminal("error!!")
        printToTerminal(err.message.toString())
    }

    return outPutData
}

fun getEnv(context: Context,key:String) : String? {
    val envPath = "environment/env"
    var foundEnv:String?=null
    var fRead:InputStream?=null
    var reader:BufferedReader?=null
    try {
        fRead  = context.assets.open(envPath)
        reader = BufferedReader(InputStreamReader(fRead))
    }
    catch (err:java.lang.Exception){
        return foundEnv
    }

    var line:String?=reader.readLine()
    while(line != null){
        val key_value = line.split(" ")
        if(key_value[0]==key){
            foundEnv = key_value[1]
            break
        }
        line = reader.readLine()
    }
    fRead.close()
    reader.close()
    return foundEnv
}


fun getPlayingCard(context: Context, filePath: String) : Bitmap {
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
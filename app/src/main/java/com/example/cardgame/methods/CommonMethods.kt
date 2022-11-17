package com.example.cardgame.methods
import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.cardgame.struct.PassedCheck
import java.io.InputStream

fun stringToInt(str : String,passedCheck:PassedCheck){
    val result : Int
    val msg : String
    try {
        result = Integer.parseInt(str)
        passedCheck.num = result
        passedCheck.result = true
        return
    }
    catch (err:java.lang.Exception){
        msg = err.message.toString()
    }
    passedCheck.msg = msg
    passedCheck.result = false
}

fun getRealSizeWidth(){

}

fun getScreenWidth() : Int{
    return getSystem().displayMetrics.widthPixels
}

fun getScreenHeight() : Int{
    return getSystem().displayMetrics.heightPixels
}

fun getRandomInt(maxSize:Int):Int{
    var rnd:Double = (Math.random()*100000000)%maxSize
    return rnd.toInt()
}

fun parseXmlDPStringToInt(str:String) : Int{
    var chr : PassedCheck = PassedCheck()
    val out = str.split(".")[0]
    stringToInt(out,chr)
    if(chr.result){
        return convertDpToPixel(chr.num)
    }
    return -1;
}

fun convertDpToPixel(value : Int):Int{
    return (value*getSystem().displayMetrics.density).toInt()
}

fun getPlayingCard(context: Context,filePath: String) : Bitmap{
    /*val imagePath = "./assets/cards/clubs_2.png"*/
    val inputStream : InputStream = context.assets.open(filePath)
    val img = BitmapFactory.decodeStream(inputStream)
    //printToTerminal("Width: ${img.width} Height: ${img.height}")
    inputStream.close()
    return img

}

fun logScreenDimensions(){
    Log.d("ScreenDimensions","Width: ${getScreenWidth()} Height: ${getScreenHeight()}")
}

fun printDeckOfCards(cards:Array<String>?){
    var i =0
    if(cards!=null){
        while(i<cards.size){
            printToTerminal(cards[i++])
        }
    }
}

fun printToTerminal(msg : String){
    Log.d("Message",msg)
}
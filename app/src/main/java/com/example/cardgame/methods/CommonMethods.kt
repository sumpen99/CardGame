package com.example.cardgame.methods
import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.cardgame.enums.PlayingCard
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

fun getCardFromPath(cardPath : String) : PlayingCard{
    val card = cardPath.split(".")[0].uppercase()
    var i = 0
    val cards = PlayingCard.values()
    while(i<cards.size){
        if(card == cards[i].name){return cards[i]}
        i++;
    }
    return PlayingCard.JOKER
}

fun getAttributes(){
    /*val h1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
    val w1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")
    val inth1 : Int = parseXmlDPStringToInt(h1)
    val intw1 : Int = parseXmlDPStringToInt(w1)
    */
}

fun setRandomPixels(){
    /*val width = bitmap.width
    val height = bitmap.height
    var x=0;var y=0;var alpha = 0xff000000
    while(y<height){
        x=0
        while(x<width){
            bitmap[x,y] = (alpha+getRandomInt(0x00ffffff)).toInt()
            x++;
        }
        y++;
    }*/
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
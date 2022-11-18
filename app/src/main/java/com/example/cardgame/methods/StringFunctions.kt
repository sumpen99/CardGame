package com.example.cardgame.methods
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.struct.PassedCheck

fun getCardFromPath(cardPath : String) : PlayingCard {
    val card = cardPath.split(".")[0].uppercase()
    var i = 0
    val cards = PlayingCard.values()
    while(i<cards.size){
        if(card == cards[i].name){return cards[i]}
        i++;
    }
    return PlayingCard.JOKER
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

fun getAttributes(){
    /*val h1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
    val w1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")
    val inth1 : Int = parseXmlDPStringToInt(h1)
    val intw1 : Int = parseXmlDPStringToInt(w1)
    */
}
package com.example.cardgame.methods
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.enums.Token
import com.example.cardgame.struct.PassedCheck

fun getJsonToken(token:Char):Token{
    if (token == ':') {return Token.JSON_OPEN_OBJECT}
    if (token == '"') {return Token.JSON_OPEN_STRING}
    if (token == '[') {return Token.JSON_OPEN_LIST}
    if (token == '{') {return Token.JSON_OPEN_DIC}
    if (token == ']') {return Token.JSON_CLOSE_LIST}
    if (token == '}') {return Token.JSON_CLOSE_DIC}
    if (token == ',') {return Token.JSON_NEW_PARAMETER}
    if (token.code >= 48 && token.code <= 57) {return Token.JSON_NUMBER_VALUE}

    if (token.code >= 65 && token.code <= 90 || token.code >= 97 && token.code <= 122){
        return Token.JSON_STRING_VALUE
    }
    return Token.JSON_TOKEN_UNKNOWN
}

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

fun cardFamilyEquals(cardOne:PlayingCard,cardTwo:PlayingCard):Boolean{
    return cardOne.cardFamily == cardTwo.cardFamily
}

fun cardIsLess(cardOne:PlayingCard,cardTwo:PlayingCard):Boolean{
    return cardOne.value < cardTwo.value
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
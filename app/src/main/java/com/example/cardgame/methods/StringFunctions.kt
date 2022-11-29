package com.example.cardgame.methods
import com.example.cardgame.enums.HttpResponse
import com.example.cardgame.enums.HttpResponse.*
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.enums.Token
import com.example.cardgame.struct.PassedCheck


fun getResponseCodeValue(code:Int): HttpResponse {
    when(code){
        200 -> return HTTP_OK
        201 -> {return HTTP_CREATED}
        202->{return HTTP_ACCEPTED}
        203->{return HTTP_NON_AUTHORITATIVE_INFORMATION}
        204->{return HTTP_NO_CONTENT}
        205->{return HTTP_RESET_CONTENT}
        206->{return HTTP_PARTIAL_CONTENT}
        300->{return HTTP_MULTIPLE_CHOICES}
        301->{return HTTP_MOVED_PERMANENTLY}
        302->{return HTTP_MOVED_TEMPORARILY}
        303->{return HTTP_SEE_OTHER}
        304->{return HTTP_NOT_MODIFIED}
        305->{return HTTP_USE_PROXY}
        306->{return HTTP_NOT_USED}
        307->{return HTTP_TEMPORARY_REDIRECT}
        308->{return HTTP_PERMAMENT_REDIRECT}
        400->{return HTTP_BAD_REQUEST}
        401->{return HTTP_UNAUTHORIZED}
        402->{return HTTP_PAYMENT_REQUIRED}
        403->{return HTTP_FORBIDDEN}
        404->{return HTTP_NOT_FOUND}
        405->{return HTTP_METHOD_NOT_ALLOWED}
        406->{return HTTP_NOT_ACCEPTABLE}
        407->{return HTTP_PROXY_AUTHENTICATION_REQUIRED}
        408->{return HTTP_REQUEST_TIMEOUT}
        409->{return HTTP_CONFLICT}
        410->{return HTTP_GONE}
        411->{return HTTP_LENGTH_REQUIRED}
        412->{return HTTP_PRECONDITION_FAILED}
        413->{return HTTP_PAYLOAD_TOO_LARGE}
        414->{return HTTP_REQUEST_URI_TOO_LONG}
        415->{return HTTP_UNSUPPORTED_MEDIA_TYPE}
        416->{return HTTP_REQUESTED_RANGE_NOT_SATISFIABLE}
        417->{return HTTP_EXPACTATION_FAILED}
        500->{return HTTP_INTERNAL_SERVER_ERROR}
        501->{return HTTP_NOT_IMPLEMENTED}
        502->{return HTTP_BAD_GATEWAY}
        503->{return HTTP_SERVICE_UNAVAILABLE}
        504->{return HTTP_GATEWAY_TIMEOUT}
        505->{return HTTP_VERSION_NOT_SUPPRTED}
        509->{return HTTP_BANDWIDTH_LIMIT_EXCEEDED}
    }
    return HTTP_RESPONSE_UNKNOWN

}

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
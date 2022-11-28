package com.example.cardgame.methods
import android.content.Context
import android.content.res.Resources.getSystem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.cardgame.enums.StringValidation
import com.example.cardgame.tree.BinarySearchTree

//val illegalChar:CharArray = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray()
val legalChars:BinarySearchTree = setLegalCharTree()

fun setLegalCharTree():BinarySearchTree{
    val legalChar:CharArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray()
    val tree = BinarySearchTree()
    for(c in legalChar){
        tree.insert(c.code)
    }
    tree.balanceTree()
    return tree
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun validString(str:String):StringValidation{
    if(str.isEmpty()){return StringValidation.STRING_IS_EMPTY}
    if(str.length> getMaxStrSize()){return StringValidation.STRING_IS_TOO_LONG}
    if(stringContainsIllegalChar(str)){return StringValidation.STRING_CONTAINS_ILLEGAL_CHAR}
    return StringValidation.STRING_IS_OK
}

fun stringContainsIllegalChar(str:String):Boolean{
    if(str.isEmpty())return true
    for(c in str.toCharArray()){
        if(!legalChars.itemExist(c.code)){return true}
    }
    return false
}

fun getRandomInt(maxSize:Int):Int{
    var rnd:Double = (Math.random()*100000000)%maxSize
    return rnd.toInt()
}

fun convertDpToPixel(value : Int):Int{
    return (value*getSystem().displayMetrics.density).toInt()
}

fun templateFunctionAny(parameter:Any?):Unit{}
fun templateFunctionInt(parameter:Int):Unit{}

fun hashKey(key:String,capacity:Int):Int{
    return Math.abs(fnv1a(key).toInt()) % capacity
}

fun fnv1a(key:String):Long{
    var hash: Long = 2166136261L
    for (i in 0 until key.length) {
        hash = hash xor key[i].code.toLong()
        hash += (hash shl 1) + (hash shl 4) + (hash shl 7) + (hash shl 8) + (hash shl 24)
    }
    return hash
}
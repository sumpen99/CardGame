package com.example.cardgame.methods
import android.content.Context
import android.content.res.Resources.getSystem
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun getRandomInt(maxSize:Int):Int{
    var rnd:Double = (Math.random()*100000000)%maxSize
    return rnd.toInt()
}

fun convertDpToPixel(value : Int):Int{
    return (value*getSystem().displayMetrics.density).toInt()
}

fun templateFunction(parameter:Any?):Unit{}

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

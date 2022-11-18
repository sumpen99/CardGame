package com.example.cardgame.methods
import android.content.res.Resources.getSystem

fun getRandomInt(maxSize:Int):Int{
    var rnd:Double = (Math.random()*100000000)%maxSize
    return rnd.toInt()
}

fun convertDpToPixel(value : Int):Int{
    return (value*getSystem().displayMetrics.density).toInt()
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

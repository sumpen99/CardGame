package com.example.cardgame.struct

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class ToastMessage(val context: Context):Toast(context){
    val longDuration:Long = 3500
    val shortDuration:Long = 2000
    var lastShown:Long = 0

    fun showMessage(message:String,duration:Int){
        if(enoughTimeHasElapsed()){
            val toast = makeText(context,message,duration)
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
            setTimer()
            toast.show()
        }
    }

    fun enoughTimeHasElapsed():Boolean{
        return System.currentTimeMillis()-lastShown > longDuration
    }

    fun setTimer(){
        lastShown = System.currentTimeMillis()
    }
}
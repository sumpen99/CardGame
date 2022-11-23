package com.example.cardgame.views
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.cardgame.interfaces.IThreading

class CounterTextView(context: Context,attrs: AttributeSet?=null):IThreading,AppCompatTextView(context, attrs){
    private var callbackInProgress:Boolean = false
    private var maxTime:Int=999

    private fun startCounter(){
        var i = 0
        while(callbackInProgress && i<=maxTime){
            text = "$i"
            Thread.sleep(1000)
            i++
        }
    }

    override fun startActivity() {
        startCounter()
    }

    override fun stopActivity() {
        callbackInProgress = false
    }

    override fun setCallbackStatus(value: Boolean) {
        callbackInProgress = value
    }

    override fun getCallbackStatus():Boolean{
        return callbackInProgress
    }

}
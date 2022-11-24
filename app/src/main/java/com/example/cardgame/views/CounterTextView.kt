package com.example.cardgame.views
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.cardgame.interfaces.IThreading

class CounterTextView(context: Context,attrs: AttributeSet?=null):IThreading,AppCompatTextView(context, attrs){
    private var callbackInProgress:Boolean = false
    private var maxTime:Int=999
    private var currentTimer:Int = 0

    private fun startCounter(){
        while(callbackInProgress && currentTimer<=maxTime){
            text = "$currentTimer"
            Thread.sleep(1000)
            currentTimer++
        }
    }

    fun getTimeTaken():Int{
        return currentTimer
    }

    fun resetClock(){
        currentTimer = 0
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
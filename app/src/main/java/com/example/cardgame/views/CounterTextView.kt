package com.example.cardgame.views
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.cardgame.interfaces.IThreading

class CounterTextView(context: Context,attrs: AttributeSet?=null):IThreading,AppCompatTextView(context, attrs){
    private var callbackInProgress:Boolean = false
    private var startedNewGame:Boolean = false
    private var userAborted:Boolean = false
    private var maxTime:Int=999
    private var currentTimer:Int = 0
    private var colorOrg:Int=0

    init{
        setEventListener()
        colorOrg = currentTextColor
    }

    private fun startCounter(){
        while(callbackInProgress && currentTimer<=maxTime){
            text = "$currentTimer"
            try{Thread.sleep(1000)}
            catch(err:Exception){setCallbackStatus(false)}
            currentTimer++
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEventListener(){
        if(userAborted){return}
        setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if(userAborted){return true}
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        setTextColor(0)
                    }
                    MotionEvent.ACTION_UP -> {
                        setTextColor(colorOrg)
                        setAbortedValue(true)
                        setCallbackStatus(false)
                    }
                }
                return true
            }
        })
    }

    fun getTimeTaken():Int{
        return currentTimer
    }

    fun resetClock(){
        text=""
        currentTimer = 0
        startedNewGame = false
        userAborted = false
    }

    fun getClockIsStarted():Boolean{
        return startedNewGame
    }

    fun setClockIsStarted(value:Boolean){
        startedNewGame = value
    }

    fun setAbortedValue(value:Boolean){
        userAborted = value
    }

    fun getAborted():Boolean{
        return userAborted
    }

    override fun startActivity(){
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
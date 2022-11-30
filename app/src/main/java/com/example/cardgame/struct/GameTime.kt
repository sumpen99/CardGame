package com.example.cardgame.struct

class GameTime {
    private var startedNewGame:Boolean = false
    private var currentTimer:Long = 0
    private var timeTaken:Int = 0

    fun getTimeTaken():Int{
        return timeTaken
    }

    fun resetClock(){
        currentTimer = 0
        startedNewGame = false
    }

    fun getClockIsStarted():Boolean{
        return startedNewGame
    }

    fun setClockIsStarted(value:Boolean){
        startedNewGame = value
        if(value){startCounting()}
        else{stopCounting()}
    }

    private fun startCounting(){
        currentTimer = System.currentTimeMillis()
    }

    private fun stopCounting() {
        timeTaken = ((System.currentTimeMillis() - currentTimer)/1000).toInt()
    }
}
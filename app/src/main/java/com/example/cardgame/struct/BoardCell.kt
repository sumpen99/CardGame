package com.example.cardgame.struct

import com.example.cardgame.enums.PlayingCard

class BoardCell {
    var x:Float = 0.0f
    var y:Float = 0.0f
    var occupied:Boolean = false
    var index:Int = -1
    lateinit var playingCard: PlayingCard

    fun setPosition(posX:Float,posY:Float,idx:Int){
        x=posX
        y=posY
        index=idx
    }

    fun setOccupied(){
        occupied = true
    }

    fun setKeyValue(cardValue:PlayingCard){
        playingCard = cardValue
    }

    fun resetCell(){
        playingCard = PlayingCard.JOKER
        index = -1
        occupied = false
        x = 0.0f
        y = 0.0f
    }

    fun makeCellFree(){
        playingCard = PlayingCard.JOKER
        occupied = false
    }
}
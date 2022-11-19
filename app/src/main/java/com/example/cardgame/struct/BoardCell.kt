package com.example.cardgame.struct

class BoardCell {
    var x:Float = 0.0f
    var y:Float = 0.0f
    var occupied:Boolean = false
    var key:Int = -1

    fun setPosition(posX:Float,posY:Float){
        x=posX
        y=posY
    }

    fun setOccupied(){
        occupied = true
    }

    fun setKeyValue(k:Int){
        key = k
    }

    fun resetCell(){
        key = -1
        occupied = false
        x = 0.0f
        y = 0.0f
    }

    fun makeCellFree(){
        key = -1
        occupied = false
    }
}
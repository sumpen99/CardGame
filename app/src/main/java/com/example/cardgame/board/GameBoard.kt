package com.example.cardgame.board
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell

class GameBoard(private var rows:Int,private var columns:Int) {
    private var size : Int = 0
    private var m: Array<BoardCell>
    init{
        size = rows*columns
        m = Array(size){ BoardCell() }
        buildCardPosition()
    }

    private fun buildCardPosition(){
        val offsetWidth = getOffsetXBetweenCards()
        val offsetHeight = getOffsetYBetweenCards()
        val cardWidth = getCardWidth()
        val cardHeight = getCardHeight()
        var rowOneX:Float = ((getScreenWidth()/2)-(offsetWidth*1.5)-(cardWidth*1.5)).toFloat()
        var colOne:Float = (cardHeight/2).toFloat()
        rowOneX-=cardWidth/2
        colOne-=cardHeight/2
        var i = 0
        while(i<size){
            val x = rowOneX+(i%columns*cardWidth)+(i%columns*offsetWidth)
            val y = colOne+(i/columns*offsetHeight)
            m[i].setPosition(x,y)
            i++
        }
    }

    fun getFreeBoardCell(index:Int):BoardCell?{
        var col = index
        while(col<size){
            if(!m[col].occupied){
                return m[col]
            }
            col+=columns
        }
        return null
    }

    fun getValue(row: Int, col: Int): BoardCell {
        return m[getIndex(row, col)]
    }

    fun setValue(row: Int, col: Int, value: BoardCell) {
        m[getIndex(row, col)] = value
    }

    fun getIndex(row: Int, col: Int): Int {
        return row * columns + col
    }

    fun getColFromIndex(index: Int): Int {
        return index % columns
    }

    fun getRowFromIndex(index: Int): Int {
        return index / columns
    }
}
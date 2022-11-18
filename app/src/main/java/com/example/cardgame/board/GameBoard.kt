package com.example.cardgame.board
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell

class GameBoard(private var rows:Int,private var columns:Int) {
    private var size : Int = 0
    private var m: Array<BoardCell>
    private var tempCount:Int = 0

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
        var colOne:Float = (getCardHeight()/2).toFloat()
        rowOneX-=cardWidth/2
        colOne-=cardHeight/2
        var i = 0
        while(i<size){
            val x = rowOneX+(i%columns*cardWidth)+(i%columns*offsetWidth)
            val y = colOne+(i/columns*offsetHeight)
            m[i].setPosition(x,y)
            i++
        }
        //printGameBoard(m)

    }

    fun getFreeBoardCell():BoardCell{
        return m[tempCount++]
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

    fun flipVertical() {
        var c: Int
        var r = 0
        var j: Int
        while (r < rows / 2) {
            c = rows - 1 - r
            j = 0
            while (j < columns) {
                val temp = m[r * columns + j]
                m[r * columns + j] = m[c * columns + j]
                m[c * columns + j] = temp
                j++
            }
            r++
        }
    }

    fun getColFromIndex(index: Int): Int {
        return index % columns
    }

    fun getRowFromIndex(index: Int): Int {
        return index / columns
    }
}
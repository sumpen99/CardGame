package com.example.cardgame.board
import com.example.cardgame.enums.Direction
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
            m[i].setPosition(x,y,i)
            i++
        }
    }

    fun validTouch(index:Int):Boolean{
        val row = getRowFromIndex(index)
        val col = getColFromIndex(index)
        if(row == rows-1 ||(validRowCol(row+1,col) && !m[getIndex(row+1,col)].occupied)){return true}
        return false
    }

    /*fun searchDirection(cellRow:Int,cellCol:Int,dir: Direction):Boolean{
        var row = cellRow
        var col = cellCol
        if(dir == Direction.NORTH){row-=1;}
        else if(dir == Direction.SOUTH){row+=1;}
        else if(dir == Direction.EAST){col+=1;}
        else if(dir == Direction.WEST){col-=1;}
        else if(dir == Direction.NORTH_EAST){row-=1;col+=1;}
        else if(dir == Direction.NORTH_WEST){row-=1;col-=1;}
        else if(dir == Direction.SOUTH_EAST){row+=1;col+=1;}
        else if(dir == Direction.SOUTH_WEST){row+=1;col-=1;}

        if(cellRow == rows-1 || (validRowCol(row,col) && !m[getIndex(row,col)].occupied)){return true}
        return false
    }*/

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

    fun validRowCol(row: Int, col: Int): Boolean {
        return row >= 0 && row < rows && col >= 0 && col < columns
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
package com.example.cardgame.board
import com.example.cardgame.enums.Direction
import com.example.cardgame.enums.StackOperation
import com.example.cardgame.methods.*
import com.example.cardgame.struct.*
import com.example.cardgame.views.CardImageView

class GameBoard(private var rows:Int,private var columns:Int) {
    private var size : Int = 0
    private var m: Array<BoardCell>
    private var reverseStack:ReverseStack = ReverseStack()
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
        rowOneX+=getFirstXOffset()
        colOne-=cardHeight/2
        colOne+=getFirstYOffset()
        var i = 0
        while(i<size){
            val x = rowOneX+(i%columns*cardWidth)+(i%columns*offsetWidth)
            val y = colOne+(i/columns*offsetHeight)
            m[i].setPosition(x,y,i)
            i++
        }
    }

    fun findClosestPoint(cardView: CardImageView):BoardCell?{
        val currentX:Float = cardView.x
        val rangeCheck = RangeCheck()
        rangeCheck.dist = Math.abs(currentX-m[0].x)
        rangeCheck.index = 0
        var i = 1
        while(i<columns){
            val dist = Math.abs(currentX-m[i].x)
            if(dist < rangeCheck.dist){
                rangeCheck.dist = dist
                rangeCheck.index = i
            }
            i++
        }
        if(!m[rangeCheck.index].occupied){
            pushMoveToStack(cardView,StackOperation.MOVE_CARD)
            return m[rangeCheck.index]
        }
        return null
    }

    fun validRemove(cardView: CardImageView):Boolean{
        val cardToTest:BoardCell = cardView.boardCell
        val row = getRowFromIndex(cardToTest.index)
        val col = getColFromIndex(cardToTest.index)
        val cardSum = PassedCheck()
        searchDirection(row,col-1,Direction.WEST,cardToTest,cardSum)
        searchDirection(row,col+1,Direction.EAST,cardToTest,cardSum)
        if(cardSum.num>0){
            pushMoveToStack(cardView,StackOperation.RETRIVE_CARD)
            return true
        }
        return false
    }

    private fun pushMoveToStack(cardView: CardImageView,op:StackOperation){
        reverseStack.push(ReverseOperation(cardView,cardView.boardCell,op))
    }

    fun popMoveFromStack(){
        reverseStack.pop()
    }

    fun clearStack(){
        reverseStack.removeHiddenCards()
        reverseStack.clearStack()
        //reverseStack = ReverseStack()
    }

    fun validTouch(index:Int):Boolean{
        val row = getRowFromIndex(index)
        val col = getColFromIndex(index)
        if(row == rows-1 ||(validRowCol(row+1,col) && !m[getIndex(row+1,col)].occupied)){return true}
        return false
    }

    private fun searchDirection(cellRow:Int,cellCol:Int,dir: Direction,cardToRemove:BoardCell,cardSum:PassedCheck){
        if(cardSum.num==0 && (cellCol>=0 && cellCol < columns)){
            if(dir == Direction.WEST){searchDirection(cellRow,cellCol-1,dir,cardToRemove,cardSum)}
            if(dir == Direction.EAST){searchDirection(cellRow,cellCol+1,dir,cardToRemove,cardSum)}
            val cardToTestAgainst = getLastCellInRow(getIndex(0,cellCol))
            if(cardToTestAgainst.occupied){
                if(cardCanBeRemoved(cardToRemove,cardToTestAgainst)){
                    cardSum.num+=1
                }
            }
        }
    }

    private fun cardCanBeRemoved(cellToRemove:BoardCell,cellToTestAgainst:BoardCell):Boolean{
        if(cardFamilyEquals(cellToRemove.playingCard,cellToTestAgainst.playingCard)){
            return cardIsLess(cellToRemove.playingCard,cellToTestAgainst.playingCard)
        }
        return false
    }

    private fun getLastCellInRow(index:Int):BoardCell{
        var currentIndex = index
        var nextIndex = index+columns
        var nextCell = getBoardCell(nextIndex)
        while(nextCell!= null && nextCell.occupied){
            currentIndex = nextIndex
            nextIndex+=columns
            nextCell = getBoardCell(nextIndex)
        }
        return m[currentIndex]
    }

    fun resetBoard(){
        clearStack()
        for(cell in m.iterator()){ cell.makeCellFree()}
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

    private fun getBoardCell(index:Int):BoardCell?{
        if(validIndex(index)){
            return m[index]
        }
        return null
    }

    private fun validIndex(index:Int):Boolean{
        return index >= 0 && index < size
    }

    private fun validRowCol(row: Int, col: Int): Boolean {
        return row >= 0 && row < rows && col >= 0 && col < columns
    }

    fun getValue(row: Int, col: Int): BoardCell {
        return m[getIndex(row, col)]
    }

    fun setValue(row: Int, col: Int, value: BoardCell) {
        m[getIndex(row, col)] = value
    }

    private fun getIndex(row: Int, col: Int): Int {
        return row * columns + col
    }

    private fun getColFromIndex(index: Int): Int {
        return index % columns
    }

    private fun getRowFromIndex(index: Int): Int {
        return index / columns
    }
}
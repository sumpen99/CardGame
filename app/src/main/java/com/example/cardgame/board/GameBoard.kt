package com.example.cardgame.board
import com.example.cardgame.enums.Direction
import com.example.cardgame.enums.StackOperation
import com.example.cardgame.methods.*
import com.example.cardgame.struct.*
import com.example.cardgame.views.CardImageView


/*
* The main class to handle gamerelated events
* */
class GameBoard(private var rows:Int,private var columns:Int) {
    private var size : Int = 0
    private var m: Array<BoardCell>
    private var reverseStack:ReverseStack = ReverseStack()
    private var allCardsDrawn:Boolean = false
    init{
        size = rows*columns
        m = Array(size){ BoardCell() }
        buildCardPosition()
    }

    /*
    * Build an array of BoardCell with coordinates to later draw cards on
    * */
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

    /*
    * when the user lets go of a card this function checks if
    * the card can move to a free spot on the board
    * It basically finds the closest column to the card and check if
    * that row is empty
    * */
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

    /*
    * if the user tries to remove a card this function takes a look
    * on every bottom card (in each column) and checks if their
    * is a card within the same family to match against
    * if it finds a card with a higher value we return true
    * otherwise we return false
    * */
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

    /*
    * In order to reverse we keep track on every move the user does
    * it currently supports move and remove (on new card we clear)
    * * */
    private fun pushMoveToStack(cardView: CardImageView,op:StackOperation){
        reverseStack.push(ReverseOperation(cardView,cardView.boardCell,op))
    }

    /*
    * when the user push the reverse button we remove that operation from the list
    * and update the board with whatever the user did
    * */
    fun popMoveFromStack(){
        reverseStack.pop()
    }

    /*
    * clears the reverse list and also removes card currently
    * up till now we have only been hiding the card on the board
    * */
    fun clearStack(){
        reverseStack.removeHiddenCards()
        reverseStack.clearStack()
        //reverseStack = ReverseStack()
    }

    /*
    * verify that the card with touchevent is at the bottom of the row
    * */
    fun validTouch(index:Int):Boolean{
        val row = getRowFromIndex(index)
        val col = getColFromIndex(index)
        if(row == rows-1 ||(validRowCol(row+1,col) && !m[getIndex(row+1,col)].occupied)){return true}
        return false
    }

    /*
    * helper function to valid touch
    * */
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

    /*
    * Match card against another
    * Same Family and lower value is needed to be removed
    * */
    private fun cardCanBeRemoved(cellToRemove:BoardCell,cellToTestAgainst:BoardCell):Boolean{
        if(cardFamilyEquals(cellToRemove.playingCard,cellToTestAgainst.playingCard)){
            return cardIsLess(cellToRemove.playingCard,cellToTestAgainst.playingCard)
        }
        return false
    }

    /*
    * If their are no more card to draw we check this function on every removed card
    * check if the user has won. Return true if their is only 4 cards on the table
    * and they are all aces
    * */
    fun detectWinner():Boolean{
        if(!getAllCardsDrawn()){return false}

        var index = columns
        var countAce = 0
        while(index<(columns*2)){
            if(m[index].occupied)return false
            countAce += if(m[index-columns].playingCard.value == getAceValue()) 1 else 0
            index++
        }
        return countAce == getWinningCount()
    }
    /*
    * helper function to valid touch -> searchdirection
    * */
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

    /*
    * reset the board
    * */
    fun resetBoard(){
        setAllCardsDrawn(false)
        clearStack()
        for(cell in m.iterator()){ cell.makeCellFree()}
    }

    fun setAllCardsDrawn(value:Boolean){
        allCardsDrawn = value
    }

    fun getAllCardsDrawn():Boolean{
        return allCardsDrawn
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
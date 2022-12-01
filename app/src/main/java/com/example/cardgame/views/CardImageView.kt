package com.example.cardgame.views
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.example.cardgame.io.getPlayingCardBitMap
import com.example.cardgame.io.printToTerminal
import com.example.cardgame.struct.BoardCell
import com.example.cardgame.struct.CardInfo


/*
* Extended ImageView with some more functionality
* */
class CardImageView(context: Context,
                    attrs: AttributeSet?=null,
                    private var cardInfo:CardInfo,
                    var boardCell:BoardCell,
                    val callbackDestroy:(CardImageView)->Unit,
                    val callbackHide:(CardImageView)->Boolean,
                    val callbackTouch:(CardImageView)->Boolean,
                    val callbackRePosition:(CardImageView)->BoardCell?) : AppCompatImageView(context, attrs){
    private var lastX:Float = 0.0f
    private var lastY:Float = 0.0f
    private var lastZ:Float = -1.0f
    private var bitmapWidth:Int = 0
    private var bitmapHeight:Int = 0
    private var onMove:Boolean = false
    private var hiddenCard:Boolean = false
    init{
        setBitMap()
        setDimension()
        setPosition()
        setBoardCell()
        storeZ()
    }

    /*
    * Reads a card from assets/cards
    * */
    private fun setBitMap(){
        val bitmap = getPlayingCardBitMap(context,"cards/${cardInfo.path}")
        bitmapWidth = bitmap.width
        bitmapHeight = bitmap.height
        setImageBitmap(bitmap)
    }

    /*
    * set the imageview size to current image size
    * */
    private fun setDimension(){
        layoutParams = ViewGroup.LayoutParams(bitmapWidth,bitmapHeight)
    }

    /*
    * Position is given from pre-built board inside gameboard class
    * */
    private fun setPosition(){
        x = boardCell.x
        y = boardCell.y
    }

    private fun setBoardCell(){
        boardCell.setOccupied()
        boardCell.setKeyValue(cardInfo.playingCard)
    }

    /*
    * if the user has removed the card but not pushed the deal card button
    * it appears as hidden. In that case we return false
    * if the card is on the move we dont haft to check the callback on touch function
    * we set z just so a moving card dont appear below any other card
    * on touch upp we check if the user want to remove a card or repositioning it
    * action cancel is needed if the user drags a card on the edge of the screen
    * if not implemented the card gets the position currently held when that event is fired
    * */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(hiddenCard){return false}
        if(onMove || callbackTouch(this)){
            //printToTerminal("${event.rawX} ${event.rawY} ${event.x} ${event.y} $x $y $width $height")
            //val pointerIndex = event.actionIndex
            //val pointerId = event.getPointerId(pointerIndex)
            //val maskedAction = event.actionMasked
            //val touchAction = event.action and MotionEvent.ACTION_MASK
            when(event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX
                    lastY = event.rawY
                    storeZ()
                    putViewOnTop()
                }
                MotionEvent.ACTION_MOVE -> {
                    setOnMove(true)
                    val mx = event.rawX - lastX
                    val my = event.rawY - lastY
                    x+=mx
                    y+=my
                    lastX = event.rawX
                    lastY = event.rawY
                }
                MotionEvent.ACTION_UP -> {
                    cardToTakeAction()
                    setOnMove(false)
                    resetZ()
                }
                /*
                MotionEvent.ACTION_POINTER_DOWN -> {}
                MotionEvent.ACTION_POINTER_UP -> {}
                MotionEvent.ACTION_CANCEL -> {}
                */
                MotionEvent.ACTION_CANCEL -> {
                    resetCardPosition()
                    setOnMove(false)
                }
            }
        }
        return true
    }

    private fun setOnMove(value:Boolean){
        onMove = value
    }

    private fun resetZ(){
        z = lastZ
    }

    private fun storeZ(){
        lastZ = z
    }

    private fun putViewOnTop(){
        z = 1.0f
    }

    /*
    * when the user drops a card we take look at what action to make
    * either remove or move it to a free spot (or no action at all)
    * */
    private fun cardToTakeAction(){
        if(!callbackHide(this)){
            val newCell = callbackRePosition(this)
            if(newCell!=null){
                setNewPosition(newCell)
                return
            }
        }
        resetCardPosition()
    }

    private fun resetCardPosition(){
        x = boardCell.x
        y = boardCell.y
        z = lastZ
    }

    /*
    * callback to main where it will be removed
    * */
    fun removeSelfFromParent(){
        callbackDestroy(this)
    }

    fun hideCardTemporary(){
        hiddenCard = true
        imageAlpha = 0
        boardCell.makeCellFree()
    }

    fun showCard(){
        hiddenCard = false
        imageAlpha = 255
        z = lastZ
        setBoardCell()
    }

    fun setNewPosition(newCell:BoardCell){
        boardCell.makeCellFree()
        boardCell = newCell
        setBoardCell()
        x = boardCell.x
        y = boardCell.y
    }

}
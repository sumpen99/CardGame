package com.example.cardgame.views
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.example.cardgame.methods.getCardFromPath
import com.example.cardgame.methods.getPlayingCard
import com.example.cardgame.struct.BoardCell

class CardImageView(context: Context,
                    attrs: AttributeSet?=null,
                    private var cardPath:String,
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
    private val CLICK_RATIO = 150
    private var leftLastClick: Long = 0
    init{
        setBitMap()
        setDimension()
        setPosition()
        setBoardCell()

    }

    private fun setBitMap(){
        val bitmap = getPlayingCard(context,"cards/${cardPath}")
        bitmapWidth = bitmap.width
        bitmapHeight = bitmap.height
        setImageBitmap(bitmap)
    }

    private fun setDimension(){
        layoutParams = ViewGroup.LayoutParams(bitmapWidth,bitmapHeight)
    }

    private fun setPosition(){
        x = boardCell.x
        y = boardCell.y
    }

    private fun setBoardCell(){
        boardCell.setOccupied()
        boardCell.setKeyValue(getCardFromPath(cardPath))
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(hiddenCard){return false}
        if(callbackTouch(this)){
            //printToTerminal("${event.rawX} ${event.rawY} ${event.x} ${event.y} $x $y $width $height")
            val pointerIndex = event.actionIndex
            val pointerId = event.getPointerId(pointerIndex)
            val maskedAction = event.actionMasked
            val touchAction = event.action and MotionEvent.ACTION_MASK
            //printToTerminal("$maskedAction")
            when(maskedAction) {
                MotionEvent.ACTION_DOWN -> {
                    if(isDoubleTap()){
                        hideSelfFromParent()
                    }
                    else{
                        lastX = event.rawX
                        lastY = event.rawY
                    }
                    //printToTerminal("$z")
                    storeZ()
                    putViewOnTop()
                    //printToTerminal("TouchDown")
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    //printToTerminal("PointerDown")

                }
                MotionEvent.ACTION_MOVE -> {
                    setBits()
                    val mx = event.rawX - lastX
                    val my = event.rawY - lastY
                    x+=mx
                    y+=my
                    lastX = event.rawX
                    lastY = event.rawY
                    //printToTerminal("TouchMove")
                }
                MotionEvent.ACTION_UP -> {
                    setDoubleTapTimer()
                    resetPosition()
                    clearBits()
                    resetZ()
                    //printToTerminal("TouchUp")
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    //printToTerminal("PointerUp")
                }
                MotionEvent.ACTION_CANCEL -> {
                    //printToTerminal("TouchCancel")
                }
            }
            //invalidate()
        }
        return true
    }

    private fun isDoubleTap():Boolean{
        return System.currentTimeMillis()-leftLastClick<=CLICK_RATIO
    }

    private fun setDoubleTapTimer(){
        leftLastClick = System.currentTimeMillis()
    }

    private fun storeZ(){
        lastZ = z
    }

    private fun putViewOnTop(){
        z = 1.0f
    }

    private fun resetZ(){
        z = lastZ
    }

    private fun setBits(){
        onMove = true
    }

    private fun clearBits(){
        onMove = false
    }

    private fun resetPosition(){
        if(onMove){
            val newCell = callbackRePosition(this)
            if(newCell!=null){
                setNewPosition(newCell)
                return
            }
            x = boardCell.x
            y = boardCell.y
        }

    }

    private fun hideSelfFromParent(){
        if(callbackHide(this)){
            hideCardTemporary()
        }
    }

    fun removeSelfFromParent(){
        callbackDestroy(this)
    }

    private fun hideCardTemporary(){
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
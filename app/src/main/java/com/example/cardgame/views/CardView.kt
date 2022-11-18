package com.example.cardgame.views
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.methods.getCardFromPath
import com.example.cardgame.methods.getPlayingCard
import com.example.cardgame.methods.printToTerminal
import com.example.cardgame.struct.BoardCell

// resource touch event
//https://www.vogella.com/tutorials/AndroidTouch/article.html

class CardView(context: Context,
               attrs: AttributeSet?=null,
               private var cardPath:String,
               private var boardCell:BoardCell,
               val callbackDestroy:(View)->Unit) : View(context, attrs){
    lateinit var bitmap : Bitmap
    lateinit var rect : Rect
    lateinit var playingCard:PlayingCard
    var lastX:Float = 0.0f
    var lastY:Float = 0.0f
    var lastZ:Float = -1.0f
    private val CLICK_RATIO = 150
    private var leftLastClick: Long = 0
    init{
        setPlayingCard()
        setBitMap()
        setDimensions()
    }

    private fun setPlayingCard(){
        playingCard = getCardFromPath(cardPath)
        //printToTerminal("${playingCard.cardFamily} ${playingCard.value}")
    }

    private fun setBitMap(){
        bitmap = getPlayingCard(context,"cards/${cardPath}")
        //bitmap = Bitmap.createBitmap(spriteWidth,spriteHeight,Bitmap.Config.ARGB_8888)
        rect = Rect(0,0,bitmap.width,bitmap.height)
    }

    private fun setDimensions(){
        layoutParams = ViewGroup.LayoutParams(bitmap.width,bitmap.height)
        //x = (getScreenWidth()/2).toFloat() - bitmap.width/2
        //y = (getScreenHeight()/2).toFloat() - bitmap.height/2
        x = boardCell.x
        y = boardCell.y

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //printToTerminal("${event.rawX} ${event.rawY} ${event.x} ${event.y} $x $y $width $height")
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val maskedAction = event.actionMasked
        val touchAction = event.action and MotionEvent.ACTION_MASK
        //printToTerminal("$maskedAction")
        when(maskedAction) {
            MotionEvent.ACTION_DOWN -> {
                if(isDoubleTap()){
                    //printToTerminal("DoubleTap")
                    removeSelfFromParent()
                }
                else{
                    lastX = event.rawX
                    lastY = event.rawY
                }
                //printToTerminal("$z")
                //storeZ()
                //putViewOnTop()
                //printToTerminal("TouchDown")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                //printToTerminal("PointerDown")

            }
            MotionEvent.ACTION_MOVE -> {
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
                //resetZ()
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

        return true
    }

    private fun removeSelfFromParent(){
        callbackDestroy(this)
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

    fun reDraw(){
        //printToTerminal("ReDrawCanvas")
        invalidate()
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        canvas.apply {
            drawBitmap(bitmap,null,rect,null)
        }

    }
}
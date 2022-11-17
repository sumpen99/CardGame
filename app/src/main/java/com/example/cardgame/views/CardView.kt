package com.example.cardgame.views
import com.example.cardgame.methods.*
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.cardgame.enums.PlayingCard


// resource touch event
//https://www.vogella.com/tutorials/AndroidTouch/article.html

class CardView(context: Context, attrs: AttributeSet?=null,private var cardPath:String) : View(context, attrs){
    lateinit var bitmap : Bitmap
    lateinit var rect : Rect
    lateinit var playingCard:PlayingCard
    var lastX:Float = 0.0f
    var lastY:Float = 0.0f
    var realWidth:Int = 0
    var realHeight:Int = 0
    var spriteWidth:Int = 0
    var spriteHeight:Int = 0
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
        //realWidth = convertDpToPixel(1000)
        //realHeight = convertDpToPixel(1000)
        //spriteWidth = convertDpToPixel(234)
        //spriteHeight = convertDpToPixel(333)
        spriteWidth = bitmap.width
        spriteHeight = bitmap.height
        realWidth = spriteWidth
        realHeight = spriteHeight
        layoutParams = ViewGroup.LayoutParams(realWidth,realHeight)
        x = (getScreenWidth()/2).toFloat() - spriteWidth/2
        y = (getScreenHeight()/2).toFloat() - spriteHeight/2
        //x = convertDpToPixel(getScreenWidth()/2).toFloat()
        //y = convertDpToPixel(getScreenHeight()/2).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //if(!insideSelf(event.x.toInt(),event.y.toInt()))return false
        //printToTerminal("${event.rawX} ${event.rawY} ${event.x} ${event.y} $x $y $width $height")
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)
        val maskedAction = event.actionMasked
        val touchAction = event.action and MotionEvent.ACTION_MASK
        //printToTerminal("$maskedAction")
        when(maskedAction) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
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
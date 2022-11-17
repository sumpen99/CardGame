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
import androidx.core.graphics.scale
import androidx.core.graphics.set


// resource touch event
//https://www.vogella.com/tutorials/AndroidTouch/article.html

class CardView(context: Context, attrs: AttributeSet?=null, var card:String) : View(context, attrs){
    lateinit var bitmap : Bitmap
    lateinit var rect : Rect
    var lastX:Float = 0.0f
    var lastY:Float = 0.0f
    var realWidth:Int = 0
    var realHeight:Int = 0
    var spriteWidth:Int = 0
    var spriteHeight:Int = 0
    init{
        setBitMap()
        setDimensions()
        //setRandomPixels()
        //setScale()
    }

    private fun getAttributes(){
        /*val h1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
        val w1 : String = attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width")
        val inth1 : Int = parseXmlDPStringToInt(h1)
        val intw1 : Int = parseXmlDPStringToInt(w1)
        */
    }

    private fun setScale(){
        bitmap.scale(100,200)
    }

    private fun setBitMap(){
        bitmap = getPlayingCard(context,"cards/${card}")
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

    /*private fun insideSelf(posX:Int,posY:Int):Boolean{
        return (posX>=0 && posX<=spriteWidth) && (posY>=0 && posY<=spriteHeight)
    }*/

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

    private fun setRandomPixels(){
        val width = bitmap.width
        val height = bitmap.height
        var x=0;var y=0;var alpha = 0xff000000
        while(y<height){
            x=0
            while(x<width){
                bitmap[x,y] = (alpha+getRandomInt(0x00ffffff)).toInt()
                x++;
            }
            y++;
        }
    }

    fun setPosition(){
        x = 100.0f
        y = 100.0f

    }

    fun reDraw(){
        //printToTerminal("ReDrawCanvas")
        invalidate()
    }

    fun addNewView(){

    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        canvas.apply {
            drawBitmap(bitmap,null,rect,null)
        }

    }
}
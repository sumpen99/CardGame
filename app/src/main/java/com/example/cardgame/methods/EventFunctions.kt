package com.example.cardgame.methods
import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton

@SuppressLint("ClickableViewAccessibility")
fun setDealCardButton(dealCardBtn:ImageButton,callbackAddNewView:(Int)->Unit){
    dealCardBtn.setOnTouchListener(object: View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dealCardBtn.imageAlpha = 127
                }
                MotionEvent.ACTION_UP -> {
                    dealCardBtn.imageAlpha=255
                    callbackAddNewView(getCardsToDraw())
                }
            }
            return true
        }
    })
}

@SuppressLint("ClickableViewAccessibility")
fun setNewGameButton(newGameBtn:ImageButton,callbackStartNewGame:()->Unit){
    newGameBtn.setOnTouchListener(object: View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    newGameBtn.imageAlpha = 127
                }
                MotionEvent.ACTION_UP -> {
                    newGameBtn.imageAlpha=255
                    callbackStartNewGame()
                }
            }
            return true
        }
    })
}

package com.example.cardgame.methods
import android.content.res.Resources
val needScaling:Boolean = getNewWidth()<250

fun getScreenWidth() : Int{
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight() : Int{
    return Resources.getSystem().displayMetrics.heightPixels
}

fun getOffsetXBetweenCards():Int{
    return 20
}

fun getOffsetYBetweenCards():Int{
    return getCardHeight()/4
}

fun getCardWidth():Int{
    if(needScaling){return getNewWidth()}
    return 250
}

fun getCardHeight():Int{
    if(needScaling){return (getNewWidth()*1.4).toInt()}
    return 350
}

fun getNewWidth():Int{
    return (getScreenWidth()-getNeededSpace())/4
}

fun getBoardRows():Int{
    return 14
}

fun getBoardCols():Int{
    return 4
}

fun getNeededSpace():Int{
    return 80
}

fun getCardsInADeck():Int{
    return 52
}
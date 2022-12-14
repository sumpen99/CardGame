package com.example.cardgame.methods
import android.content.res.Resources
val needScaling:Boolean = getNewWidth()<250

fun getMaxStrSize():Int{
    return 255
}

fun getScreenWidth() : Int{
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight() : Int{
    return Resources.getSystem().displayMetrics.heightPixels
}

fun getFirstXOffset():Int{
    return 10
}

fun getFirstYOffset():Int{
    return 20
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

fun getCardsToDraw():Int{
    return 4
}

fun getDecksToUse():Int{
    return 1
}

fun getAceValue():Int{
    return 14
}

fun getWinningCount():Int{
    return 4
}

fun getCardRatio():Float{
    return 1.4.toFloat()
}

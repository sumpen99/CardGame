package com.example.cardgame.methods
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.example.cardgame.struct.BoardCell
import java.io.InputStream

fun getPlayingCard(context: Context, filePath: String) : Bitmap {
    //printToTerminal("$needScaling")
    /*val imagePath = "./assets/cards/clubs_2.png"*/
    val inputStream : InputStream = context.assets.open(filePath)
    var img = BitmapFactory.decodeStream(inputStream)
    if(needScaling){
        val width = getNewWidth()
        img = img.scale(width,(width*1.4).toInt(),true)
    }
    //printToTerminal("Width: ${img.width} Height: ${img.height}")
    inputStream.close()
    return img

}

fun logScreenDimensions(){
    Log.d("ScreenDimensions","Width: ${getScreenWidth()} Height: ${getScreenHeight()}")
}

fun printGameBoard(gameBoard:Array<BoardCell>){
    var i = 0
    while(i<gameBoard.size){
        printToTerminal("${gameBoard[i].x} ${gameBoard[i].y}")
        i++
    }

}

fun printDeckOfCards(cards:Array<String>?){
    var i =0
    if(cards!=null){
        while(i<cards.size){
            printToTerminal(cards[i++])
        }
    }
}

fun printToTerminal(msg : String){
    Log.d("Message",msg)
}
package com.example.cardgame.methods
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.example.cardgame.struct.BoardCell
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

fun getEnv(context: Context,key:String) : String? {
    val envPath = "environment/env"
    var foundEnv:String?=null
    val fRead  = context.assets.open(envPath)
    val reader:BufferedReader = BufferedReader(InputStreamReader(fRead))
    var line:String?=reader.readLine()
    while(line != null){
        val key_value = line.split(" ")
        if(key_value[0]==key){
            foundEnv = key_value[1]
            break
        }
        line = reader.readLine()
    }
    reader.close()

    return foundEnv
}


fun getPlayingCard(context: Context, filePath: String) : Bitmap {
    //printToTerminal("$needScaling")
    /*val imagePath = "./assets/cards/clubs_2.png"*/
    val inputStream : InputStream = context.assets.open(filePath)
    var img = BitmapFactory.decodeStream(inputStream)
    if(needScaling){
        val width = getNewWidth()
        img = img.scale(width,(width* getCardRatio()).toInt(),true)
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
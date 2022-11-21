package com.example.cardgame.struct
import com.example.cardgame.views.CardImageView

/*
* TODO SUPPORT REMOVED CARDS AS WELL
* */
class ReverseOperation(val cardView:CardImageView,val boardCellOrg: BoardCell){
    var next:ReverseOperation?=null
    fun reverseMove(){
        cardView.setNewPosition(boardCellOrg)
    }
}
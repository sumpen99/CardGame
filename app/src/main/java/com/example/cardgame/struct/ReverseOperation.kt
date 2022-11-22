package com.example.cardgame.struct
import com.example.cardgame.views.CardImageView

class ReverseOperation(val cardView:CardImageView,val boardCellOrg: BoardCell){
    var next:ReverseOperation?=null
    fun reverseMove(){
        cardView.setNewPosition(boardCellOrg)
    }
}
package com.example.cardgame.struct
import com.example.cardgame.enums.StackOperation
import com.example.cardgame.views.CardImageView

class ReverseOperation(val cardView:CardImageView,val boardCellOrg: BoardCell,val opCode:StackOperation){
    var next:ReverseOperation?=null
    fun reverseMove(){
        if(opCode == StackOperation.MOVE_CARD){ cardView.setNewPosition(boardCellOrg)}
        else if(opCode==StackOperation.RETRIVE_CARD){cardView.showCard()}
    }
}
package com.example.cardgame.struct
import com.example.cardgame.enums.StackOperation
import com.example.cardgame.io.printToTerminal

/*
* Simple linkedlist with the intention of keeping track of
* action taken by the user
* Valid reverse operations is move a card to previous position and regret a
* removed card
* when new cards being added their is not possibility to reverse
* */
class ReverseStack {
    var root:ReverseOperation?=null

    fun push(child:ReverseOperation){
        if(root==null){root=child}
        else{
            child.next = root
            root = child
        }
    }

    fun pop(){
        if(root!=null){
            val temp:ReverseOperation = root!!
            temp.reverseMove()
            root = temp.next
        }
    }

    fun removeHiddenCards(){
        if(root!=null){
            var t = root
            while(t!=null){
                if(t.opCode == StackOperation.RETRIVE_CARD){
                    t.cardView.removeSelfFromParent()
                }
                t = t.next
            }
        }
    }

    fun clearStack(){
        root = null
    }

    fun printStack(){
        var count = 0
        if(root!=null){
            var t = root
            while(t!=null){
                count++
                printToTerminal(t.cardView.toString())
                t = t.next
            }
        }
        printToTerminal("$count Objects On Current Stack")
    }
}
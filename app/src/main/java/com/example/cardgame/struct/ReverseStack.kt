package com.example.cardgame.struct

import com.example.cardgame.methods.printToTerminal

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
            temp.cardView.setNewPosition(temp.boardCellOrg)
            root = temp.next
        }
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
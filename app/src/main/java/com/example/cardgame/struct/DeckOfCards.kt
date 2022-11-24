package com.example.cardgame.struct
import com.example.cardgame.methods.getCardsInADeck
import com.example.cardgame.methods.getDecksToUse
import com.example.cardgame.methods.getRandomInt
import com.example.cardgame.tree.BinarySearchTree

class DeckOfCards (private val deckOfCards:Array<String>){
    lateinit var shuffledDeck:MutableList<Int>
    val decksToUse = getDecksToUse()
    val numCards = decksToUse*getCardsInADeck()
    var nextCard:Int = 0

    init{
        setDeck()
        shuffleDeck()
    }

    private fun setDeck(){
        shuffledDeck = MutableList(numCards){0}
    }

    private fun shuffleDeck(){
        val treeOfPlayingCards = BinarySearchTree()
        var i = 0
        var rndCard:Int
        while(i<numCards){
            rndCard = getRandomInt(deckOfCards.size)
            while(treeOfPlayingCards.itemReachedMaxCount(rndCard,decksToUse)){
                rndCard = getRandomInt(deckOfCards.size)
            }
            treeOfPlayingCards.insert(rndCard)
            shuffledDeck[i]=rndCard
            i++
        }
    }

    fun resetDeck(){
        nextCard = 0
        shuffleDeck()
    }

    fun getNextCardInDeck():String{
        return deckOfCards[shuffledDeck[nextCard++]]
    }

}
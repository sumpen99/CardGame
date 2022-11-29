package com.example.cardgame.struct
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.methods.getCardsInADeck
import com.example.cardgame.methods.getDecksToUse
import com.example.cardgame.methods.getRandomInt
import com.example.cardgame.tree.BinarySearchTree

class DeckOfCards{
    private lateinit var shuffledDeck:List<CardInfo>
    private val decksToUse = getDecksToUse()
    private val numCards = decksToUse*getCardsInADeck()
    var nextCard:Int = 0

    init{
        setDeck()
        shuffleDeck()
    }

    private fun setDeck(){
        shuffledDeck = List(numCards){ CardInfo() }
    }

    private fun shuffleDeck(){
        val deckOfCards = PlayingCard.values()
        val treeOfPlayingCards = BinarySearchTree()
        var i = 0
        var rndCard:Int
        while(i<numCards){
            while(treeOfPlayingCards.itemReachedMaxCount((getRandomInt(getCardsInADeck())).also{rndCard=it},decksToUse)){}
            treeOfPlayingCards.insert(rndCard)
            shuffledDeck[i].setValues(deckOfCards[rndCard])
            i++
        }
        //printDeckOfCards(shuffledDeck)
    }

    fun resetDeck(){
        nextCard = 0
        shuffleDeck()
    }

    fun getNextCardInDeck():CardInfo{
        return shuffledDeck[nextCard++]
    }

}
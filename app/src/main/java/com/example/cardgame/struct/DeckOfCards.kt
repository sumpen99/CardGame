package com.example.cardgame.struct
import com.example.cardgame.enums.PlayingCard
import com.example.cardgame.io.printToTerminal
import com.example.cardgame.methods.getCardsInADeck
import com.example.cardgame.methods.getCardsToDraw
import com.example.cardgame.methods.getDecksToUse
import com.example.cardgame.methods.getRandomInt
import com.example.cardgame.tree.BinarySearchTree

class DeckOfCards (private val deckOfCards:Array<String>){
    private lateinit var shuffledDeck:List<CardInfo>
    private var mapOfCards = mutableMapOf<String,PlayingCard>()
    private val decksToUse = getDecksToUse()
    private val numCards = decksToUse*getCardsInADeck()
    var nextCard:Int = 0

    init{
        setDeck()
        mapCardPathToPlayingCard()
        shuffleDeck()
    }

    private fun setDeck(){
        shuffledDeck = List(numCards){ CardInfo() }
    }

    private fun mapCardPathToPlayingCard(){
        val cards = PlayingCard.values()
        var i = 0
        while(i<cards.size){
            mapOfCards[cards[i].name] = cards[i]
            i++;
        }
    }

    private fun getPlayingCardFromPath(path:String):PlayingCard{
        val cardName = path.split(".")[0].uppercase()
        return mapOfCards[cardName]!!
    }

    private fun shuffleDeck(){
        val treeOfPlayingCards = BinarySearchTree()
        var i = 0
        var rndCard:Int
        while(i<numCards){
            while(treeOfPlayingCards.itemReachedMaxCount((getRandomInt(deckOfCards.size)).also{rndCard=it},decksToUse)){}
            treeOfPlayingCards.insert(rndCard)
            shuffledDeck[i].setValues(deckOfCards[rndCard],getPlayingCardFromPath(deckOfCards[rndCard]))
            i++
        }
    }

    fun resetDeck(){
        nextCard = 0
        shuffleDeck()
    }

    fun getNextCardInDeck():CardInfo{
        return shuffledDeck[nextCard++]
    }

}
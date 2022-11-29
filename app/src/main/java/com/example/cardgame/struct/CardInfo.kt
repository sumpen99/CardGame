package com.example.cardgame.struct
import com.example.cardgame.enums.PlayingCard

class CardInfo{
    lateinit var playingCard:PlayingCard
    lateinit var path:String

    fun setValues(currentCard:PlayingCard){
        playingCard = currentCard
        path = "${currentCard.name.lowercase()}.png"
    }
}
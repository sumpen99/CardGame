package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.struct.DeckOfCards
import com.example.cardgame.views.CardView

class MainActivity : AppCompatActivity() {
    private lateinit var deckOfCards : DeckOfCards
    private lateinit var gameBoard:GameBoard
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var cardsDrawn : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logScreenDimensions()
        loadCards()
        setUpGameBoard()
        setDataBinding()
        setEventListener()
        addNewView(getCardsToDraw())
    }

    private fun setUpGameBoard(){
        gameBoard = GameBoard(getBoardRows(),getBoardCols())
    }

    private fun loadCards(){
        deckOfCards = DeckOfCards(assets.list("cards")!!)
    }

    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setEventListener(){
        val txtBtn = binding.mainBtn
        txtBtn.setOnClickListener(){
            addNewView(getCardsToDraw())
        }
    }

    private fun addNewView(cardsToAdd:Int){
        if(cardsDrawn > getCardsInADeck()-cardsToAdd)return
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            assert(boardCell!=null) // if null we done something terrible wrong
            binding.cardViewLayout.addView(
                CardView(this,null,deckOfCards.getNextCardInDeck(),boardCell!!,::removeCardView),
                binding.cardViewLayout.childCount)
            i++
            cardsDrawn++
        }
    }

    private fun removeCardView(cardView: View){
        binding.cardViewLayout.removeView(cardView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
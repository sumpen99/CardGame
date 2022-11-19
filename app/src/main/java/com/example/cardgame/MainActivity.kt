package com.example.cardgame
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setEventListener(){
        val imgBtn = binding.mainBtn
        /*txtBtn.setOnClickListener(){
            addNewView(getCardsToDraw())
        }*/

        imgBtn.setOnTouchListener(object:OnTouchListener{
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        imgBtn.imageAlpha = 127
                    }
                    MotionEvent.ACTION_UP -> {
                        imgBtn.imageAlpha=255
                        addNewView(getCardsToDraw())
                    }
                }
                return true
            }
        })
    }

    private fun addNewView(cardsToAdd:Int){
        if(cardsDrawn > getCardsInADeck()-cardsToAdd)return
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            assert(boardCell!=null) // if null we done something terrible wrong
            binding.cardViewLayout.addView(
                CardView(this,null,deckOfCards.getNextCardInDeck(),boardCell!!,::removeCardView,::cardViewIsFree),
                binding.cardViewLayout.childCount)
            i++
            cardsDrawn++
        }
    }

    /**
     * CALLBACK TO CHECK IF CARDVIEW WITH TOUCH IS AT BOTTOM OF ROW
     * */
    private fun cardViewIsFree(cardView: CardView):Boolean{
        return gameBoard.validTouch(cardView.boardCell.index)
    }

    /**
     * CALLBACK TO REMOVE CARDVIEW FROM GAMEBOARD
     * */
    private fun removeCardView(cardView: CardView){
        cardView.boardCell.makeCellFree()
        binding.cardViewLayout.removeView(cardView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.children
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell
import com.example.cardgame.struct.DeckOfCards
import com.example.cardgame.views.CardImageView

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
        val dealCardBtn = binding.dealCardBtn
        val newGameBtn = binding.newGameBtn
        val reverseBtn = binding.reverseBtn
        val settingsBtn = binding.settingsBtn
        setDealCardButton(dealCardBtn,::addNewView)
        setNewGameButton(newGameBtn,::startNewGame)
    }

    private fun addNewView(cardsToAdd:Int){
        if(cardsDrawn > getCardsInADeck()-cardsToAdd)return
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            //assert(boardCell!=null) { "Ooops"}
            binding.cardViewLayout.addView(
                CardImageView(this,null,deckOfCards.getNextCardInDeck(),boardCell!!,::removeCardView,::cardViewIsFree,::cardViewRePosition),
                binding.cardViewLayout.childCount)
            i++
            cardsDrawn++
        }
    }

    private fun startNewGame(){
        cardsDrawn = 0
        gameBoard.resetBoard()
        deckOfCards.resetDeck()
        clearCardViewsFromLayout()
        addNewView(getCardsToDraw())
    }

    /*
    * Needs several loops to remove all children
    * */
    private fun clearCardViewsFromLayout(){
        while(binding.cardViewLayout.childCount>1){
            var i = 1
            val childCount = binding.cardViewLayout.childCount
            while(i<childCount){
                binding.cardViewLayout.removeView(binding.cardViewLayout.getChildAt(i))
                i++
            }
            if(childCount==1){break}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    //    ############################ CALLBACKS ############################

    /**
     * CHECK IF CARDVIEW CAN MOVE TO A FREE SPOT
     *
     * */
    private fun cardViewRePosition(cardView: CardImageView):BoardCell?{
        return gameBoard.findClosestPoint(cardView.x)
    }

    /**
     * CHECK IF CARDVIEW WITH TOUCH IS AT BOTTOM OF ROW
     *
     * */
    private fun cardViewIsFree(cardView: CardImageView):Boolean{
        return gameBoard.validTouch(cardView.boardCell.index)
    }
    /**
     * REMOVE CARDVIEW FROM GAMEBOARD IF ITS A VALID GAMEMOVE
     *
     * */
    private fun removeCardView(cardView: CardImageView){
        if(gameBoard.validRemove(cardView.boardCell)){
            cardView.boardCell.makeCellFree()
            binding.cardViewLayout.removeView(cardView)
        }
    }

}
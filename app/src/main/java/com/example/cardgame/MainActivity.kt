package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.tree.BinarySearchTree
import com.example.cardgame.views.CardView

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private var deckOfCards : Array<String>? = null
    private val binding get() = _binding!!
    private val treeOfPlayingCards:BinarySearchTree = BinarySearchTree()
    private lateinit var gameBoard:GameBoard
    private var cardsDrawn : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logScreenDimensions()
        loadCards()
        setUpGameBoard()
        setDataBinding()
        setEventListener()
    }

    private fun setUpGameBoard(){
        gameBoard = GameBoard(getBoardRows(),getBoardCols())
    }

    private fun loadCards(){
        deckOfCards = assets.list("cards")
    }

    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setEventListener(){
        val txtBtn = binding.mainBtn
        txtBtn.setOnClickListener(){
            addNewView()
        }
    }

    private fun addNewView(){
        if(cardsDrawn == getCardsInADeck())return
        var rndCard:Int = getRandomInt(deckOfCards!!.size)
        while(treeOfPlayingCards.itemExist(rndCard)){
            rndCard = getRandomInt(deckOfCards!!.size)
        }

        //printToTerminal("$rndCard")
        if(!treeOfPlayingCards.isBalanced()){treeOfPlayingCards.balanceTree()}

        cardsDrawn++
        treeOfPlayingCards.insert(rndCard)
        val cell = gameBoard.getFreeBoardCell()
        assert(cell!=null) // if null we done something terrible wrong
        binding.cardViewLayout.addView(
            CardView(this,null,deckOfCards!![rndCard],cell!!,::removeCardView),
            binding.cardViewLayout.childCount)
    }

    private fun removeCardView(cardView: View){
        binding.cardViewLayout.removeView(cardView)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
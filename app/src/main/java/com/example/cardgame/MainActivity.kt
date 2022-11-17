package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.tree.BinarySearchTree
import com.example.cardgame.views.CardView

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private var deckOfCards : Array<String>? = null
    private val binding get() = _binding!!
    private val treeOfPlayingCards:BinarySearchTree = BinarySearchTree()
    private var cardsDrawn : Int = 0
    private val CARDS_IN_A_DECK : Int = 52

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logScreenDimensions()
        loadCards()
        printDeckOfCards(deckOfCards)
        setDataBinding()
        setEventListener()
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
        if(cardsDrawn == CARDS_IN_A_DECK)return
        var rndCard:Int = getRandomInt(deckOfCards!!.size)
        while(treeOfPlayingCards.itemExist(rndCard)){
            rndCard = getRandomInt(deckOfCards!!.size)
        }

        //printToTerminal("$rndCard")
        if(cardsDrawn == 10 && !treeOfPlayingCards.isBalanced()){treeOfPlayingCards.balanceTree()}
        
        cardsDrawn++
        treeOfPlayingCards.insert(rndCard)
        binding.spriteViewLayout.addView(CardView(this,null,deckOfCards!![rndCard]),binding.spriteViewLayout.childCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
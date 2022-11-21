package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell
import com.example.cardgame.struct.DeckOfCards
import com.example.cardgame.struct.MessageToUser
import com.example.cardgame.views.CardImageView
import com.google.android.material.bottomnavigation.BottomNavigationView


/*
* TODO EXTEND REVERSE TO SUPPORT REMOVED CARD AS WELL AS MOVED CARDS (STORE BITMAPS AFTER DEL?)
* TODO MAKE NEW BUTTONS -> NEW GAME -> REVERSE MOVE
* TODO MAKE RULES PAGE
* TODO MAKE UP SOME SETTINGS -> ADD WILD CARD (JOKER?)
* TODO MAKE SOME KIND OF MESSAGE AFTER WIN/LOSS
* */



class MainActivity : AppCompatActivity() {
    private lateinit var deckOfCards : DeckOfCards
    private lateinit var gameBoard:GameBoard
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var cardsDrawn : Int = 0
    private val rulesFragment = RulesFragment()
    private val settingsFragment = SettingsFragment()
    private var currentFragment:Fragment? = null

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
        val bottomNavMenu: BottomNavigationView = binding.bottomNavigationView
        dealCardBtn.setCallback(getCardsToDraw(),::addNewView)
        newGameBtn.setCallback(null,::startNewGame)
        reverseBtn.setCallback(null,::reverseLastMove)
        bottomNavMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navHome->removeCurrentFragment()
                R.id.navRules->navigateFragment(rulesFragment)
                R.id.navSettings->navigateFragment(settingsFragment)
            }
            true
        }

    }

    private fun addNewView(parameter:Any?){
        val cardsToAdd:Int = parameter as Int
        if(cardsDrawn > getCardsInADeck()-cardsToAdd)return
        gameBoard.clearStack()
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            binding.cardViewLayout.addView(
                CardImageView(this,null,deckOfCards.getNextCardInDeck(),boardCell!!,::removeCardView,::cardViewIsFree,::cardViewRePosition),
                binding.cardViewLayout.childCount)
            i++
            cardsDrawn++
        }
    }

    private fun navigateFragment(fragment:Fragment){
        //if(currentFragment==fragment){removeCurrentFragment();return}
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainLayout,fragment).commit()
            currentFragment = fragment
        }
    }

    private fun removeCurrentFragment(){
        if(currentFragment!=null){
            supportFragmentManager.beginTransaction().remove(currentFragment!!).commit()
            currentFragment = null
        }
    }

    private fun startNewGame(parameter:Any?){
        MessageToUser(this,null,"Testing")
        cardsDrawn = 0
        gameBoard.resetBoard()
        deckOfCards.resetDeck()
        clearCardViewsFromLayout()
        addNewView(getCardsToDraw())
    }

    private fun reverseLastMove(parameter:Any?){
        gameBoard.popMoveFromStack()
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
        return gameBoard.findClosestPoint(cardView)
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
            gameBoard.clearStack()
            cardView.boardCell.makeCellFree()
            binding.cardViewLayout.removeView(cardView)
        }
    }

}
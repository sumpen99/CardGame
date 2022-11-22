package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell
import com.example.cardgame.struct.DeckOfCards
import com.example.cardgame.struct.MessageToUser
import com.example.cardgame.struct.ToastMessage
import com.example.cardgame.views.CardImageView
import com.google.android.material.bottomnavigation.BottomNavigationView


/*
* TODO MAKE RULES PAGE
* TODO MAKE UP SOME SETTINGS -> ADD WILD CARD (JOKER?)
* TODO MAKE SOME KIND OF MESSAGE AFTER WIN/LOSS
* */



class MainActivity : AppCompatActivity() {
    private lateinit var deckOfCards : DeckOfCards
    private lateinit var gameBoard:GameBoard
    private lateinit var infoToUser:ToastMessage
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
        setUpInfoToUser()
        setEventListener()
        addNewView(getCardsToDraw())
    }

    private fun setUpInfoToUser(){
        infoToUser = ToastMessage(this)
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
        bottomNavMenu.setOnItemSelectedListener {it: MenuItem ->
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
        if(cardsDrawn > getCardsInADeck()-cardsToAdd){
            informUser("No More Cards To Draw...")
            return
        }
        clearStack()
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            binding.cardViewLayout.addView(
                CardImageView(this,null,deckOfCards.getNextCardInDeck(),boardCell!!,::removeCardView,::hideCardView,::cardViewIsFree,::cardViewRePosition),
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

    private fun informUser(message:String){
        infoToUser.showMessage(message,Toast.LENGTH_SHORT)
    }

    private fun startNewGame(parameter:Any?){
        MessageToUser(this,null,null,::playerStartNewGame,"Star New Game?")
    }

    private fun clearStack(){
        gameBoard.clearStack()
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
     *              START NEW GAME IF USER CLICK YES
     * */
    private fun playerStartNewGame(parameter:Any?){
        cardsDrawn = 0
        gameBoard.resetBoard()
        deckOfCards.resetDeck()
        clearCardViewsFromLayout()
        addNewView(getCardsToDraw())
    }

    /**
     *              CHECK IF CARDVIEW CAN MOVE TO A FREE SPOT
     * */
    private fun cardViewRePosition(cardView: CardImageView):BoardCell?{
        return gameBoard.findClosestPoint(cardView)
    }

    /**
     *              CHECK IF CARDVIEW WITH TOUCH IS AT BOTTOM OF ROW
     * */
    private fun cardViewIsFree(cardView: CardImageView):Boolean{
        return gameBoard.validTouch(cardView.boardCell.index)
    }

    /**
     *              HIDE CARDVIEW FROM GAMEBOARD IF ITS A VALID GAMEMOVE
     * */
    private fun hideCardView(cardView: CardImageView):Boolean{
        return gameBoard.validRemove(cardView)
    }

    /**
     *              REMOVE CARDVIEW FROM GAMEBOARD
     * */
    private fun removeCardView(cardView: CardImageView){
        binding.cardViewLayout.removeView(cardView)
    }
}
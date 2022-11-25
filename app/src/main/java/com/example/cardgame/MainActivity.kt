package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.struct.BoardCell
import com.example.cardgame.struct.DeckOfCards
import com.example.cardgame.struct.MessageToUser
import com.example.cardgame.struct.ToastMessage
import com.example.cardgame.threading.executeNewThread
import com.example.cardgame.views.CardImageView
import com.example.cardgame.views.CounterTextView
import com.google.android.material.bottomnavigation.BottomNavigationView


/*
* TODO MAKE RULES PAGE
* TODO MAKE UP SOME SETTINGS -> ADD WILD CARD (JOKER?)
*
* */



class MainActivity : AppCompatActivity() {
    private lateinit var counterTxt: CounterTextView
    private lateinit var deckOfCards : DeckOfCards
    private lateinit var gameBoard:GameBoard
    private lateinit var infoToUser:ToastMessage
    private lateinit var bottomNavMenu:BottomNavigationView
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var cardsDrawn : Int = 0
    private var currentFragment:Fragment? = null
    private var firstRun:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logScreenDimensions()
        loadCards()
        setUpGameBoard()
        setDataBinding()
        setUpInfoToUser()
        setUpNavMenu()
        setEventListener()
    }

    private fun setUpNavMenu(){
        bottomNavMenu = binding.bottomNavigationView
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
        counterTxt = binding.counterTxtView

        dealCardBtn.setCallback(getCardsToDraw(),::addNewView)
        newGameBtn.setCallback(null,::askForNewGame)
        reverseBtn.setCallback(null,::reverseLastMove)
        bottomNavMenu.setOnItemSelectedListener {it: MenuItem ->
            when(it.itemId){
                R.id.navHome->removeCurrentFragment(true)
                R.id.navRules->navigateFragment(RulesFragment())
                R.id.navSettings->navigateFragment(SettingsFragment())
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
        if(!counterTxt.getClockIsStarted()){return}
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
        if(cardsDrawn == getCardsInADeck()*getDecksToUse()){
            gameBoard.setAllCardsDrawn(true)
        }
    }

    private fun navigateFragment(fragment:Fragment){
        stopClock()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainLayout,fragment).commit()
            currentFragment = fragment
        }
    }

    private fun removeCurrentFragment(restartClock:Boolean){
        if(currentFragment!=null){
            supportFragmentManager.beginTransaction().remove(currentFragment!!).commit()
            currentFragment = null
            if(restartClock){startClock()}
        }
    }

    private fun informUser(message:String){
        infoToUser.showMessage(message,Toast.LENGTH_SHORT)
    }

    private fun askForNewGame(parameter:Any?){
        if(firstRun){
            firstRun = false
            playerStartNewGame(null)
        }
        else{
            stopClock()
            MessageToUser(this,null,null,::playerStartNewGame,::playerResumeGame,"Start New Game?")
        }
    }

    private fun resetFirstRun(){
        firstRun = true
    }

    private fun clearStack(){
        gameBoard.clearStack()
    }

    private fun reverseLastMove(parameter:Any?){
        gameBoard.popMoveFromStack()
    }

    private fun startClock(){
        counterTxt.setClockIsStarted(true)
        executeNewThread(counterTxt)
    }

    private fun stopClock(){
        counterTxt.setClockIsStarted(false)
        counterTxt.stopActivity()
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

    private fun launchWinnerScreen(){
        switchNavBarOnTouch(false)
        navigateFragment(WinnerFragment(counterTxt.getTimeTaken(),::closeWinnerScreen,::closeWinnerScreen))
    }

    private fun closeWinnerScreen(parameter:Any?){
        switchNavBarOnTouch(true)
        removeCurrentFragment(false)
        resetProgram()
        resetFirstRun()
    }

    private fun switchNavBarOnTouch(value:Boolean){
        bottomNavMenu.menu.forEach { it.isEnabled = value }
    }

    private fun resetProgram(){
        cardsDrawn = 0
        gameBoard.resetBoard()
        deckOfCards.resetDeck()
        counterTxt.resetClock()
        clearCardViewsFromLayout()
    }

    override fun onPause() {
        printToTerminal("OnPause")
        super.onPause()
    }

    override fun onStop() {
        printToTerminal("OnStop")
        super.onStop()
    }

    override fun onResume() {
        printToTerminal("OnResume")
        super.onResume()
    }

    override fun onDestroy() {
        printToTerminal("OnDestroy")
        super.onDestroy()
        _binding = null
    }


    //    ############################ CALLBACKS ############################

    /**
     *              RESUME GAME IF USER CLICK YES AND RESTART CLOCK
     * */
    private fun playerResumeGame(parameter:Any?){
        startClock()
    }


    /**
     *              START NEW GAME IF USER CLICK YES AND RESTART CLOCK
     * */
    private fun playerStartNewGame(parameter:Any?){
        resetProgram()
        startClock()
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
     *              IF ALL CARDS IS DRAWN, CHECK FOR A WINNING MOVE
     * */
    private fun hideCardView(cardView: CardImageView):Unit{
        if(gameBoard.validRemove(cardView)){
            cardView.hideCardTemporary()
            if(!gameBoard.detectWinner()){
                launchWinnerScreen()
            }
        }
    }

    /**
     *              REMOVE CARDVIEW FROM GAMEBOARD
     * */
    private fun removeCardView(cardView: CardImageView){
        binding.cardViewLayout.removeView(cardView)
    }
}
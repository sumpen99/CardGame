package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.example.cardgame.api.ApiHandler
import com.example.cardgame.board.GameBoard
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.enums.ApiFunction
import com.example.cardgame.fragment.HighScoreFragment
import com.example.cardgame.fragment.RulesFragment
import com.example.cardgame.fragment.WinnerFragment
import com.example.cardgame.io.printToTerminal
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
        checkForCertications(this)
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
                R.id.navHome->removeCurrentFragment()
                R.id.navRules->navigateFragment(RulesFragment())
                R.id.navHighScore->launchHighScoreScreen()
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
        stopClock()
        switchNavBarOnTouch(false)
        navigateFragment(WinnerFragment(counterTxt.getTimeTaken(),::closeWinnerScreen,::sendScoreToServer))
    }

    private fun launchHighScoreScreen(){
        //stopClock()
        //switchNavBarOnTouch(false)
        val highScoreTable:Array<String> = Array(10){""}
        if(getHighScoreFromServer(highScoreTable)){
            navigateFragment(HighScoreFragment(highScoreTable))
        }
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
        super.onPause()
        printToTerminal("OnPause")
    }

    override fun onStop() {
        super.onStop()
        printToTerminal("OnStop")
    }

    override fun onResume() {
        super.onResume()
        printToTerminal("OnResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        printToTerminal("OnDestroy")
        _binding = null
    }


    //    ############################ CALLBACKS ############################

    /**
     *              CLOSE FRAGMENT WINNER WITHOUT SENDING SCORE TO SERVER
     * */
    private fun sendScoreToServer(parameter:Any?){
        val apiObject = ApiHandler(this,null,null)
        verifyApiService(this)
        if(apiServiceIsOk()){
            //printToTerminal("UserName:${userName_userScore[0]} UserScore:${userName_userScore[1]}")
            apiObject.setApiService(ApiFunction.URL_UPLOAD_HIGHSCORE)
            apiObject.setArguments(parameter as Array<String>)
            executeNewThread(apiObject)
        }
        else{
            informUser(getApiErrorMessage())
        }
        closeWinnerScreen(null)
    }

    /**
     *              CLOSE FRAGMENT WINNER WITHOUT SENDING SCORE TO SERVER
     * */
    private fun getHighScoreFromServer(highScoreTable:Array<String>):Boolean{
        val apiObject = ApiHandler(this,null,null)
        verifyApiService(this)
        if(apiServiceIsOk()){
            apiObject.setApiService(ApiFunction.URL_GET_HIGHSCORE)
            apiObject.setArguments(highScoreTable)
            executeNewThread(apiObject)
            return true
        }
        else{
            informUser(getApiErrorMessage())
        }
        return false
    }

    /**
     *              CLOSE FRAGMENT WINNER
     * */
    private fun closeWinnerScreen(parameter:Any?){
        switchNavBarOnTouch(true)
        removeCurrentFragment()
        resetProgram()
        resetFirstRun()
    }

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
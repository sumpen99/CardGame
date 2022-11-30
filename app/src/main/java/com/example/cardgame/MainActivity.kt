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
import com.example.cardgame.enums.FragmentInstance
import com.example.cardgame.enums.HttpResponse
import com.example.cardgame.fragment.HighScoreFragment
import com.example.cardgame.fragment.RulesFragment
import com.example.cardgame.fragment.WinnerFragment
import com.example.cardgame.interfaces.IFragment
import com.example.cardgame.io.printToTerminal
import com.example.cardgame.io.setAppEnvVariables
import com.example.cardgame.methods.*
import com.example.cardgame.struct.*
import com.example.cardgame.threading.executeNewThread
import com.example.cardgame.views.CardImageView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var gameTimer:GameTime
    private lateinit var deckOfCards:DeckOfCards
    private lateinit var gameBoard:GameBoard
    private lateinit var infoToUser:ToastMessage
    private lateinit var bottomNavMenu:BottomNavigationView
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var cardsDrawn : Int = 0
    private var currentFragment: Fragment? = null
    private var firstRun:Boolean = true
    private var childrenToNotRemove:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //logScreenDimensions()
        loadCards()
        setGameTimer()
        setUpGameBoard()
        setDataBinding()
        setUpInfoToUser()
        setUpNavMenu()
        setChildrenNotToRemove()
        setEventListener()
        setAppEnvVariables(this)
        checkForCertications()
    }

    //      #############################################################################
    //                                  INIT WIDGETS AND STRUCTS
    //      #############################################################################

    private fun setGameTimer(){
        gameTimer = GameTime()
    }

    private fun setChildrenNotToRemove(){
        childrenToNotRemove = binding.cardViewLayout.childCount
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
        deckOfCards = DeckOfCards()
    }

    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    //      #############################################################################
    //                                  SET EVENT-LISTENER
    //      #############################################################################

    private fun setEventListener(){
        val dealCardBtn = binding.dealCardBtn
        val newGameBtn = binding.newGameBtn
        val reverseBtn = binding.reverseBtn

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

    //      #############################################################################
    //                                  HANDLE NEW SCREENS
    //      #############################################################################

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

    private fun launchWinnerScreen(){
        stopClock()
        switchNavBarOnTouch(false)
        navigateFragment(WinnerFragment(gameTimer.getTimeTaken(),::closeWinnerScreen,::sendScoreToServer))
    }

    private fun launchHighScoreScreen(){
        navigateFragment(HighScoreFragment())
        getHighScoreFromServer()

    }

    //      #############################################################################
    //                                  MESSAGE TO USER
    //      #############################################################################

    private fun informUser(message:String){
        infoToUser.showMessage(message,Toast.LENGTH_SHORT)
    }


    //      #############################################################################
    //                                  RESET OPERATIONS
    //      #############################################################################

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
        gameTimer.setClockIsStarted(true)
    }

    private fun stopClock(){
        gameTimer.setClockIsStarted(false)
    }

    private fun clearCardViewsFromLayout(){
        while(binding.cardViewLayout.childCount>childrenToNotRemove){
            var i = childrenToNotRemove
            val childCount = binding.cardViewLayout.childCount
            while(i<childCount){
                binding.cardViewLayout.removeView(binding.cardViewLayout.getChildAt(i))
                i++
            }
        }
    }

    private fun switchNavBarOnTouch(value:Boolean){
        bottomNavMenu.menu.forEach { it.isEnabled = value }
    }

    private fun resetProgram(){
        cardsDrawn = 0
        gameBoard.resetBoard()
        deckOfCards.resetDeck()
        gameTimer.resetClock()
        clearCardViewsFromLayout()
    }

    //      #############################################################################
    //                                  ON APP CLOSE/OPEN
    //      #############################################################################

    override fun onPause() {
        super.onPause()
        //printToTerminal("OnPause")
    }

    override fun onStop() {
        super.onStop()
        //printToTerminal("OnStop")
    }

    override fun onResume() {
        super.onResume()
        //printToTerminal("OnResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        //printToTerminal("OnDestroy")
        _binding = null
    }


    //      #############################################################################
    //                                  CALLBACKS
    //      #############################################################################

    /**
     *              ADD NEW CARD TO THE TABLE (4ST)
     * */
    private fun addNewView(parameter:Any?){
        val cardsToAdd:Int = parameter as Int
        if(cardsDrawn > getCardsInADeck()-cardsToAdd){
            informUser("No More Cards To Draw...")
            return
        }
        if(!gameTimer.getClockIsStarted()){return}
        clearStack()
        var i = 0
        while(i<cardsToAdd){
            val boardCell = gameBoard.getFreeBoardCell(i)
            val cardInfo = deckOfCards.getNextCardInDeck()
            binding.cardViewLayout.addView(
                CardImageView(this,null,cardInfo,boardCell!!,::removeCardView,::hideCardView,::cardViewIsFree,::cardViewRePosition),
                binding.cardViewLayout.childCount)
            i++
            cardsDrawn++
        }
        if(cardsDrawn == getCardsInADeck()*getDecksToUse()){
            gameBoard.setAllCardsDrawn(true)
        }
    }

    /**
     *              ASK USER FOR A NEW GAME
     * */
    private fun askForNewGame(parameter:Any?){
        if(firstRun){
            firstRun = false
            playerStartNewGame(null)
        }
        else{
            MessageToUser(this,null,null,::playerStartNewGame,"Start New Game?")
        }
    }


    /**
     *              SEND SCORE TO SERVER IF NEEDED CERTIFICATES IS PRESENT AND
     *              INTERNETCONNECTION IS AVAILABLE
     * */
    private fun sendScoreToServer(parameter:Any?){
        val apiObject = ApiHandler(this,null,null)
        apiObject.setCallbackResponseCode(::showApiResponseCode)
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
     *              GET HIGHSCORE FROM SERVER IF NEEDED CERTIFICATES IS PRESENT AND
     *              INTERNETCONNECTION IS AVAILABLE
     *
     * */
    private fun getHighScoreFromServer():Boolean{
        val apiObject = ApiHandler(this,null,null)
        apiObject.setCallbackResponseCode(::showApiResponseCode)
        apiObject.setCallbackFinished(::populateHighScoreTable)
        verifyApiService(this)
        if(apiServiceIsOk()){
            apiObject.setApiService(ApiFunction.URL_GET_HIGHSCORE)
            executeNewThread(apiObject)
            return true
        }
        else{
            informUser(getApiErrorMessage())
        }
        return false
    }

    /**
     *              POPULATE HIGHSCORETABLE WITH DATA (ON MAINTHREAD) WHEN SERVER THREAD IS DONE
     *              IF THREAD HAS TAKEN TO MUCH TIME AND THE USER SHIFT VIEW -> DO NOTHING
     *
     * */
    private fun populateHighScoreTable(parameter:Any?){
        // TODO SHOW MESSAGE IF SERVER DISCONNECTED
        if(currentFragment!=null && (currentFragment as IFragment).getFragmentID() == FragmentInstance.FRAGMENT_HIGHSCORE){
            try{
                Thread.currentThread().apply { this@MainActivity.runOnUiThread(java.lang.Runnable {
                    (currentFragment as IFragment).processWork(parameter)
                })}
            }
            catch(err:Exception){
                printToTerminal(err.message.toString())
            }
        }
    }

    /**
     *              SHOW RESPONSECODE FROM SERVER, MOSTLY FOR TESTING
     *
     * */
    private fun showApiResponseCode(responseCode:HttpResponse){
        try{
            Thread.currentThread().apply { this@MainActivity.runOnUiThread(java.lang.Runnable {
                infoToUser.showMessage(responseCode.name,Toast.LENGTH_SHORT)
            })}
        }
        catch(err:Exception){
            printToTerminal(err.message.toString())
        }
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
    private fun hideCardView(cardView: CardImageView):Boolean{
        if(gameBoard.validRemove(cardView)){
            cardView.hideCardTemporary()
            if(gameBoard.detectWinner()){
                launchWinnerScreen()
            }
            return true
        }
        return false
    }

    /**
     *              REMOVE CARDVIEW FROM GAMEBOARD
     * */
    private fun removeCardView(cardView: CardImageView){
        binding.cardViewLayout.removeView(cardView)
    }
}
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

    /*
    * Timer used to count score if the user win
    * */
    private fun setGameTimer(){
        gameTimer = GameTime()
    }

    /*
    * Used to prevent the logo in the middle of the screen to be removed
    * whenever we start a new game and clear the screen
    * */
    private fun setChildrenNotToRemove(){
        childrenToNotRemove = binding.cardViewLayout.childCount
    }

    /*
    * Bottomnavigation menu
    * */
    private fun setUpNavMenu(){
        bottomNavMenu = binding.bottomNavigationView
    }

    /*
    * Only reason for this is to prevent multiple Toasts being activated
    * if the user keeps pressing the deal card button when there are no more cards to be drawn
    * */
    private fun setUpInfoToUser(){
        infoToUser = ToastMessage(this)
    }

    /*
    * This class handles positioning of the cards and check valid moves osv
    * */
    private fun setUpGameBoard(){
        gameBoard = GameBoard(getBoardRows(),getBoardCols())
    }

    private fun loadCards(){
        deckOfCards = DeckOfCards()
    }

    /*
    * One way I found to access widgets by id
    * AndroidStudio sometimes gives import errors but they can be ignored
    * */
    private fun setDataBinding(){
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    //      #############################################################################
    //                                  SET EVENT-LISTENER
    //      #############################################################################

    /*
    * Set up buttons with respective callback-function
    * */
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

    /*
    * Navigate between frames and keep track of which one is launched
    * */
    private fun navigateFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.mainLayout,fragment).commit()
            currentFragment = fragment
        }
    }

    /*
    * Remove current fragment and return to main screen
    * */
    private fun removeCurrentFragment(){
        if(currentFragment!=null){
            supportFragmentManager.beginTransaction().remove(currentFragment!!).commit()
            currentFragment = null
        }
    }

    /*
    * If we have a winner navigate to winnerscreen
    * Tell bottom menu to not activate functions on pressed button
    * */
    private fun launchWinnerScreen(){
        stopClock()
        switchNavBarOnTouch(false)
        navigateFragment(WinnerFragment(gameTimer.getTimeTaken(),::closeWinnerScreen,::sendScoreToServer))
    }

    /*
    * Make call to server and populate highscore table with data
    * If credentials is missing the frame still opens but a toastmessage with error is also shown
    * */
    private fun launchHighScoreScreen(){
        navigateFragment(HighScoreFragment())
        getHighScoreFromServer()

    }

    //      #############################################################################
    //                                  MESSAGE TO USER
    //      #############################################################################

    /*
    * Show message
    * */
    private fun informUser(message:String){
        infoToUser.showMessage(message,Toast.LENGTH_SHORT)
    }


    //      #############################################################################
    //                                  RESET OPERATIONS
    //      #############################################################################

    /*
    * Used to skip messagedialog AskForNewGame when its not needed
    * */
    private fun resetFirstRun(){
        firstRun = true
    }

    /*
    * Clears the backtrack list every time the user deals new cards
    * */
    private fun clearStack(){
        gameBoard.clearStack()
    }

    /*
    * When the reverse button is pressed
    * */
    private fun reverseLastMove(parameter:Any?){
        gameBoard.popMoveFromStack()
    }

    private fun startClock(){
        gameTimer.setClockIsStarted(true)
    }

    private fun stopClock(){
        gameTimer.setClockIsStarted(false)
    }

    /*
    * If we start a new game we need to clear the board from current ImageViews
    * It does not work properly without the outer loop
    * childrenToNotRemove is every child set inside the layout file ( logo in the middle)
    * */
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

    /*
    * Sets functionality on touch
    * */
    private fun switchNavBarOnTouch(value:Boolean){
        bottomNavMenu.menu.forEach { it.isEnabled = value }
    }

    /*
    * reset stuff that needs to be reset
    * */
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

    /*
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

    /*
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


    /*
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

    /*
     *              GET HIGHSCORE FROM SERVER IF NEEDED CERTIFICATES IS PRESENT AND
     *              INTERNETCONNECTION IS AVAILABLE
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

    /*
     *              POPULATE HIGHSCORETABLE WITH DATA (ON MAINTHREAD) WHEN SERVER THREAD IS DONE
     *              IF THREAD HAS TAKEN TO MUCH TIME AND THE USER SHIFT VIEW -> DO NOTHING
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

    /*
     *              SHOW RESPONSECODE FROM SERVER, MOSTLY FOR TESTING
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


    /*
     *              CLOSE FRAGMENT WINNER
     * */
    private fun closeWinnerScreen(parameter:Any?){
        switchNavBarOnTouch(true)
        removeCurrentFragment()
        resetProgram()
        resetFirstRun()
    }

    /*
     *              START NEW GAME IF USER CLICK YES AND RESTART CLOCK
     * */
    private fun playerStartNewGame(parameter:Any?){
        resetProgram()
        startClock()
        addNewView(getCardsToDraw())
    }

    /*
     *              CHECK IF CARDVIEW CAN MOVE TO A FREE SPOT
     * */
    private fun cardViewRePosition(cardView: CardImageView):BoardCell?{
        return gameBoard.findClosestPoint(cardView)
    }

    /*
     *              CHECK IF CARDVIEW WITH TOUCH IS AT BOTTOM OF ROW
     * */
    private fun cardViewIsFree(cardView: CardImageView):Boolean{
        return gameBoard.validTouch(cardView.boardCell.index)
    }

    /*
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

    /*
     *              REMOVE CARDVIEW FROM GAMEBOARD
     * */
    private fun removeCardView(cardView: CardImageView){
        binding.cardViewLayout.removeView(cardView)
    }
}
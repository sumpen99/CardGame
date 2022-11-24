package com.example.cardgame
import androidx.fragment.app.Fragment
import com.example.cardgame.methods.printToTerminal

class WinnerFragment(private val timeTaken:Int) : Fragment(R.layout.fragment_winner) {
    init{
        printToTerminal("$timeTaken")
    }
}
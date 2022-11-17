package com.example.cardgame
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cardgame.databinding.ActivityMainBinding
import com.example.cardgame.methods.*
import com.example.cardgame.views.CardView

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private var deckOfCards : Array<String>? = null
    private val binding get() = _binding!!

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
        val rndCard = getRandomInt(deckOfCards!!.size)
        binding.spriteViewLayout.addView(CardView(this,null,deckOfCards!![rndCard]),binding.spriteViewLayout.childCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.example.cardgame.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cardgame.R
import com.example.cardgame.databinding.FragmentHighscoreBinding
import com.example.cardgame.enums.FragmentInstance
import com.example.cardgame.interfaces.IFragment
import com.example.cardgame.widget.CustomImageButton
import com.example.cardgame.widget.CustomTableRow

class HighScoreFragment(var highScoreTable:Array<String>) : IFragment, Fragment(R.layout.fragment_highscore) {

    private var _binding: FragmentHighscoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentID: FragmentInstance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentHighscoreBinding.inflate(inflater,container,false)
        setFragmentID()
        //val view: View = binding.root
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun populateData(highScoreTable:Array<String>){
        val table = binding.tableLayout
        var i = 0
        while(i<highScoreTable.size){
            table.addView(CustomTableRow(context,null,"1","Fredrik Sundström","999"))
            i++
        }
    }

    override fun processWork(parameter:Any?){

        
    }

    override fun setFragmentID(){
        fragmentID = FragmentInstance.FRAGMENT_HIGHSCORE
    }

    override fun getFragmentID(): FragmentInstance {
        return fragmentID
    }


}
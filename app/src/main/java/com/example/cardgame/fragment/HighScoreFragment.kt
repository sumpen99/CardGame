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
import com.example.cardgame.widget.CustomImageButton
import com.example.cardgame.widget.CustomTableRow

class HighScoreFragment(var highScoreTable:Array<String>) : Fragment(R.layout.fragment_highscore) {

    private var _binding: FragmentHighscoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonClose: CustomImageButton
    private lateinit var buttonSubmit: CustomImageButton
    private lateinit var editTextName: EditText
    private lateinit var textViewTime: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentHighscoreBinding.inflate(inflater,container,false)
        val view: View = binding.root
        val table = binding.tableLayout
        var i = 0
        while(i++<100){
            table.addView(CustomTableRow(context,null,"1","Fredrik Sundström","999"))
        }
        //setEventListener(view)
        //setButtons()
        return view
    }


}
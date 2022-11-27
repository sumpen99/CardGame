package com.example.cardgame.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import com.example.cardgame.R
import com.example.cardgame.databinding.FragmentHighscoreBinding
import com.example.cardgame.enums.FragmentInstance
import com.example.cardgame.interfaces.IFragment
import com.example.cardgame.struct.TableRowValues
import com.example.cardgame.widget.CustomTableRow

class HighScoreFragment : IFragment, Fragment(R.layout.fragment_highscore) {

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

    fun populateTable(){
        val table:TableLayout = binding.tableLayout
        var i = 0
        while(i<10){
            table.addView(CustomTableRow(context,null,"${(i+1)}","Fredrik","999"))
            i++
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun processWork(parameter:Any?){
        if(parameter!=null){
            val personData = parameter as Array<TableRowValues>
            val table = binding.tableLayout
            var i = 0
            while(i<personData.size){
                table.addView(CustomTableRow(context,null,personData[i].index,personData[i].name,personData[i].score))
                i++
            }


        }
    }

    override fun setFragmentID(){
        fragmentID = FragmentInstance.FRAGMENT_HIGHSCORE
    }

    override fun getFragmentID(): FragmentInstance {
        return fragmentID
    }


}
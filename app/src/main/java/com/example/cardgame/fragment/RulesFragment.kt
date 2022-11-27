package com.example.cardgame.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cardgame.R
import com.example.cardgame.databinding.FragmentRulesBinding
import com.example.cardgame.enums.FragmentInstance
import com.example.cardgame.interfaces.IFragment

class RulesFragment :IFragment, Fragment(R.layout.fragment_rules) {
    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentID: FragmentInstance

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentRulesBinding.inflate(inflater,container,false)
        setFragmentID()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun processWork(parameter:Any?){}

    override fun setFragmentID(){
        fragmentID = FragmentInstance.FRAGMENT_RULES
    }

    override fun getFragmentID(): FragmentInstance {
        return fragmentID
    }
}

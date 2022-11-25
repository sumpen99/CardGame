package com.example.cardgame
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cardgame.databinding.FragmentWinnerBinding
import com.example.cardgame.widget.CustomImageButton

class WinnerFragment(private val timeTaken:Int,
                     private val callbackClose:(Any?)->Unit,
                     private val callbackSubmit:(Any?)->Unit,) : Fragment(R.layout.fragment_winner){
    private var _binding: FragmentWinnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonClose:CustomImageButton
    private lateinit var buttonSubmit:CustomImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentWinnerBinding.inflate(inflater,container,false)
        val view: View = binding.root
        setButtons()
        return view
    }

    private fun setButtons(){
        buttonClose = binding.closeWinnerBtn
        buttonSubmit = binding.submitWinnerBtn
        buttonClose.setCallback(null,callbackClose)
        buttonSubmit.setCallback(null,callbackSubmit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
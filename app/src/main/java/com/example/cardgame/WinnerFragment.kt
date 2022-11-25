package com.example.cardgame
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cardgame.databinding.FragmentWinnerBinding
import com.example.cardgame.methods.hideKeyboard
import com.example.cardgame.widget.CustomImageButton

class WinnerFragment(
    private val timeTaken: Int,
    private val callbackClose: (Any?) -> Unit,
    private val callbackSubmit: (Any?) -> Unit,
) : Fragment(R.layout.fragment_winner){
    private var _binding: FragmentWinnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonClose:CustomImageButton
    private lateinit var buttonSubmit:CustomImageButton
    private lateinit var editTextName:EditText
    private lateinit var textViewTime: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentWinnerBinding.inflate(inflater,container,false)
        val view: View = binding.root
        view.setOnTouchListener { v, event ->
            when(event.actionMasked){
                MotionEvent.ACTION_DOWN -> {editTextName.hideKeyboard()}
                MotionEvent.ACTION_POINTER_DOWN -> {}
                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {}
                MotionEvent.ACTION_POINTER_UP -> {}
                MotionEvent.ACTION_CANCEL -> {}
            }
            true
        }
        setButtons()
        return view
    }

    private fun setButtons(){
        textViewTime = binding.textViewTime
        editTextName = binding.editText
        buttonClose = binding.closeWinnerBtn
        buttonSubmit = binding.submitWinnerBtn
        textViewTime.text = "$timeTaken"
        buttonClose.setCallback(null,callbackClose)
        buttonSubmit.setCallback(null,callbackSubmit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
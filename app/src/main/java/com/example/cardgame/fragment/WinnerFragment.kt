package com.example.cardgame.fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cardgame.R
import com.example.cardgame.databinding.FragmentWinnerBinding
import com.example.cardgame.enums.FragmentInstance
import com.example.cardgame.enums.StringValidation
import com.example.cardgame.interfaces.IFragment
import com.example.cardgame.methods.hideKeyboard
import com.example.cardgame.methods.validString
import com.example.cardgame.struct.ToastMessage
import com.example.cardgame.widget.CustomImageButton

class WinnerFragment(
    private val userAborted:Boolean,
    private val timeTaken: Int,
    private val callbackClose: (Any?) -> Unit,
    private val callbackSubmit: (Any?) -> Unit) :IFragment, Fragment(R.layout.fragment_winner){
    private var _binding: FragmentWinnerBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentID: FragmentInstance
    private lateinit var buttonClose:CustomImageButton
    private lateinit var buttonSubmit:CustomImageButton
    private lateinit var editTextName:EditText
    private lateinit var textViewTime: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentWinnerBinding.inflate(inflater,container,false)
        val view: View = binding.root
        setEventListener(view)
        setButtons()
        setFragmentID()
        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setEventListener(view:View){
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
    }

    private fun setButtons(){
        textViewTime = binding.textViewTime
        editTextName = binding.editText
        buttonClose = binding.closeWinnerBtn
        buttonSubmit = binding.submitWinnerBtn
        textViewTime.text = verifyIfTimeIsLegit()
        buttonClose.setCallback(null,callbackClose)
        if(!userAborted){buttonSubmit.setCallback(null,::uploadScoreToServer)}
        else{buttonSubmit.setCallback(null,::rejectUpload)}
    }

    private fun verifyIfTimeIsLegit():String{
        if(!userAborted){return "$timeTaken"}
        else{
            return "Wait..We have Reasons To Believe Your Winning Score Might Be A Little Of..."
        }
    }

    private fun uploadScoreToServer(parameter:Any?):Unit{
        val nameValid:StringValidation = validString(editTextName.text.toString())
        if(nameValid != StringValidation.STRING_IS_OK){
            ToastMessage(requireContext()).showMessage(nameValid.message,Toast.LENGTH_SHORT)
        }
        else{
            val userCred:Array<String> = arrayOf(editTextName.text.toString(),"$timeTaken")
            callbackSubmit(userCred)
        }
    }

    private fun rejectUpload(parameter:Any?):Unit{
        val msg = "Only Valid Scores get Uploaded.."
        ToastMessage(requireContext()).showMessage(msg,Toast.LENGTH_SHORT)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun processWork(parameter:Any?){}

    override fun setFragmentID(){
        fragmentID = FragmentInstance.FRAGMENT_WINNER
    }

    override fun getFragmentID(): FragmentInstance {
        return fragmentID
    }

}
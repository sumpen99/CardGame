package com.example.cardgame.interfaces

import com.example.cardgame.enums.FragmentInstance

interface IFragment {
    fun setFragmentID()
    fun processWork(parameter:Any?)
    fun getFragmentID():FragmentInstance
}
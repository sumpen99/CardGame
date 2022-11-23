package com.example.cardgame.interfaces

interface IThreading {
    fun startActivity()
    fun stopActivity()
    fun setCallbackStatus(value:Boolean)
    fun getCallbackStatus():Boolean
}
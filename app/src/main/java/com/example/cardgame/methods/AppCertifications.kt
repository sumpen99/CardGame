package com.example.cardgame.methods
import android.content.Context
import com.example.cardgame.api.ApiHandler
import com.example.cardgame.io.getEnv

private var apiServiceValid:Boolean = true
private var apiErrorMessage:String? = null

fun apiServiceIsOk():Boolean{
    return apiServiceValid
}

fun verifyApiService(context:Context){
    apiErrorMessage = null
    apiServiceValid = true
    checkForCertications(context)
    val apiObject = ApiHandler(context,null,null)
    if(!apiObject.checkInternetConnectivity()){
        apiServiceValid = false
        setApiErrorMessage("No Internet Connection")
    }
}

fun checkForCertications(context: Context){
    if(getEnv(context,"username")==null){
        setApiErrorMessage("Missing SSL Certificate")
        apiServiceValid = false
    }
}

fun setApiErrorMessage(str:String){
    if(apiErrorMessage== null){
        apiErrorMessage = str
    }
    else{
        apiErrorMessage+= " || $str"
    }
}

fun getApiErrorMessage():String{
    return apiErrorMessage!!
}


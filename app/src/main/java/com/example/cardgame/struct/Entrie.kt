package com.example.cardgame.struct

import com.example.cardgame.enums.EntrieType

class Entrie(var key:String,var value:Any?,var bucket:Int,var eType:EntrieType?,var set:Boolean) {
    constructor():this("",null,0,null,false)
    var next:Entrie?=null

    fun setValues(bucket:Int,key:String,value:Any,eType: EntrieType){
        this.bucket = bucket
        this.key = key
        this.value = value
        this.set = true
        this.eType = eType
        this.next = null
    }
}
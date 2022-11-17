package com.example.cardgame.struct

class PassedCheck(var num:Int,var result : Boolean,var msg:String){
    constructor() : this(0,false,"")
    constructor(num:Int) : this(num,false,"")
    constructor(num:Int,result:Boolean) : this(0,result,"")
    constructor(result:Boolean,msg:String) : this(0,result,msg)
}
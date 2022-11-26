package com.example.cardgame.json
import com.example.cardgame.map.SMHashMap

class JsonList(var size:Int){
    var objMaps:Array<SMHashMap> = Array(size){ SMHashMap(100,.75f) }
    var objCount:Int=0
}
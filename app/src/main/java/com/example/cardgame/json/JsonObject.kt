package com.example.cardgame.json
import com.example.cardgame.enums.EntrieType
import com.example.cardgame.enums.Token
import com.example.cardgame.enums.Token.*
import com.example.cardgame.io.printToTerminal
import com.example.cardgame.map.SMHashMap
import com.example.cardgame.methods.getJsonToken
import com.example.cardgame.struct.RangeCheck
import com.example.cardgame.struct.TableRowValues

// EXPECTED FORMAT FROM SERVER
//{"detail":"Invalid token."}
//{"token":"00ae00c000a0fcb000d3d4598bac4c84e22474ce"}
//[{"id":2,"name":"Fredrik","score":100},{"id":3,"name":"Johan","score":99},...]
//{"id":2,"name":"Fredrik","score":100}
class JsonObject(var rawData:String) {
    var objMap:SMHashMap
    lateinit var objList:JsonList
    var objCount:Int=0
    var listCount:Int=0
    var dataSize:Int=0

    init{
        dataSize = rawData.length
        objMap = SMHashMap(100,.75f)
        parseData()
    }

    fun getHighScoreValues():Array<TableRowValues>?{
        val table:Array<TableRowValues>?
        if (listCount > 0) {
            table = Array(objList.objCount){TableRowValues()}
            var i = 0
            while(i<objList.objCount){
                val objMap = objList.objMaps[i]
                val name = objMap.getValue("name") as String
                val score = objMap.getValue("score") as String
                table[i].setValues("${i+1}",name,score)
                i++
            }
            return  table
        }
        else if (objCount > 0) {
            table = Array(1){TableRowValues()}
            val name = objMap.getValue("name") as String
            val score = objMap.getValue("score") as String
            table[0].setValues("1",name,score)
            return  table
        }
        return null
    }

    fun getServerResponse():String {
        if (objCount > 0) {
            return objMap.getValue("message") as String
        }
        return "Server Did Not Respond Correctly"
    }

    private fun parseData() {
        val i = RangeCheck()
        var token: Char
        while(i.index < dataSize){
            token = rawData[i.index]
            if(getJsonToken(token) === JSON_OPEN_DIC) {
                objCount++
                parseDictionary(i, objMap)
            }
            else if (getJsonToken(token) === JSON_OPEN_LIST) {
                val objCount = getEntrieCount(i.index)
                objList = JsonList(objCount)
                listCount++
                parseListObject(i, objList)
            }
            i.index++
        }
    }

    private fun parseDictionary(j: RangeCheck, hashMap: SMHashMap){
        var objCount: Int
        var value: String = ""
        val next = RangeCheck()
        next.index = j.index
        var token: Token
        while (validIndex(next.index) && getJsonToken(rawData[next.index]) !== JSON_CLOSE_DIC) {
            while (getJsonToken(rawData[next.index]) !== JSON_OPEN_STRING) {
                next.index++
            }
            val root = parseStringObject(next)
            //printToTerminal(root);
            ++next.index
            token = getJsonToken(rawData[next.index])
            if (token === JSON_OPEN_LIST) {
                objCount = getEntrieCount(next.index)
                val jsonList = JsonList(objCount)
                hashMap.addNewItem(root, jsonList, EntrieType.ENTRIE_JSON_LIST)
                parseListObject(next, jsonList)
            } else if (token === JSON_OPEN_STRING) {
                value = parseStringObject(next)
                //printToTerminal(value);
                hashMap.addNewItem(root, value, EntrieType.ENTRIE_JSON_STRING)
            } else if (token === JSON_NUMBER_VALUE) {
                value = parseStringNumbers(next)
                //printToTerminal(value);
                hashMap.addNewItem(root, value, EntrieType.ENTRIE_JSON_STRING)
            } else if (token === JSON_STRING_VALUE) {
                value = parseStringNumbers(next)
                hashMap.addNewItem(root, value, EntrieType.ENTRIE_JSON_STRING)
            }
            next.index++
        }
        j.index = next.index
    }

    private fun parseListObject(j: RangeCheck, list: JsonList) {
        val next = RangeCheck()
        next.index = j.index + 1
        var token: Token
        while(getJsonToken(rawData[next.index]).also { token = it } !== JSON_CLOSE_LIST) {
            if (token === JSON_OPEN_DIC) {
                parseDictionary(next, list.objMaps[list.objCount++])
            }
            next.index++
        }
        j.index = next.index
    }

    private fun parseStringObject(j: RangeCheck): String {
        var strBuf = ""
        var skip: Int = j.index + 1
        while (getJsonToken(rawData[skip]) !== JSON_OPEN_STRING) {
            strBuf += rawData[skip++]
        }
        j.index = skip + 1
        return strBuf
    }

    private fun parseStringNumbers(j: RangeCheck): String {
        var strBuf = ""
        var skip: Int = j.index
        while (getJsonToken(rawData[skip]) === JSON_NUMBER_VALUE) {
            strBuf += rawData[skip++]
        }
        j.index = skip - 1
        return strBuf
    }

    private fun getEntrieCount(j: Int): Int {
        var skip:Int= j
        var obj_cnt:Int= 0
        //skip = j
        //obj_cnt = 0
        var token: Token
        while (getJsonToken(rawData[skip++]).also { token = it } !== JSON_CLOSE_LIST) {
            if (token === JSON_CLOSE_DIC) {
                obj_cnt++
            }
        }
        return obj_cnt
    }

    private fun validIndex(index: Int): Boolean {
        return index < rawData.length
    }
}
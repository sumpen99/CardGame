package com.example.cardgame.map
import com.example.cardgame.enums.EntrieType
import com.example.cardgame.methods.hashKey
import com.example.cardgame.struct.Entrie

/*
* Like the json-object its purely for fun and also a bit educating
* On apps used by other people built in datastructures is to be recommended
*
* */

class SMHashMap(var capacity: Int,var loadFactor: Float) {
    var count = 0
    lateinit var entries:Array<Entrie>

    init{
        hashmapInit()
    }

    private fun hashmapInit() {
        entries = Array<Entrie>(capacity){Entrie()}
    }

    private fun expandTable() {
        val temp: SMHashMap
        var h1: Entrie?
        var h2: Entrie
        val oldCapacity = capacity
        capacity *= 2
        temp = SMHashMap(capacity, loadFactor)
        var i = 0
        while(i < oldCapacity) {
            if (entries[i].set){
                h1 = entries[i]
                while (h1 != null) {
                    val bucket: Int = hashKey(h1.key, capacity)
                    h1 = if (temp.entries[bucket].set){
                        extendCollision(temp.entries[bucket], h1)
                    }
                    else{
                        temp.entries[bucket].setValues(bucket, h1.key, h1.value!!, h1.eType!!)
                        h1.next
                    }
                }
            }
            i++
        }
        entries = temp.entries
    }

    fun addNewItem(key: String, value: Any, eType: EntrieType) {
        assert(!containsKey(key)) { "ID Already In Use" }
        val bucket: Int = hashKey(key, capacity)
        if(!entries[bucket].set){
            entries[bucket].setValues(bucket, key, value, eType)
        }
        else{
            val e = Entrie(key, value, bucket, eType,true)
            addCollision(entries[bucket], e)
        }
        count++
        if((count / capacity).toFloat() > loadFactor){expandTable()}
    }

    private fun addCollision(base: Entrie, item: Entrie) {
        item.next = base.next
        base.next = item
    }

    private fun extendCollision(base: Entrie, item: Entrie): Entrie? {
        var e: Entrie? = null
        if(item.next != null) {
            e = Entrie(item.next!!.key, item.next!!.value, item.next!!.bucket, item.next!!.eType,true)
        }
        item.next = base.next
        base.next = item
        return e
    }

    private fun containsKey(key: String): Boolean {
        var e: Entrie?
        val bucket: Int = hashKey(key, capacity)
        if (entries[bucket].set) {
            e = entries[bucket]
            while(e != null) {
                if(e.key==key){return true}
                e = e.next
            }
        }
        return false
    }

    fun getObject(key: String): Entrie? {
        var e: Entrie?
        val bucket: Int = hashKey(key, capacity)
        if (entries[bucket].set) {
            e = entries[bucket]
            while(e != null){
                if (e.key.equals(key)){return e}
                e = e.next
            }
        }
        return null
    }

    fun getValue(key: String): Any?{
        var e: Entrie?
        val bucket: Int = hashKey(key, capacity)
        if (entries[bucket].set){
            e = entries[bucket]
            while(e != null) {
                if(e.key.equals(key)){return e.value}
                e = e.next
            }
        }
        return null
    }

    fun bucketIsSet(key: String): Boolean{
        val bucket: Int = hashKey(key, capacity)
        return entries[bucket].set
    }
}
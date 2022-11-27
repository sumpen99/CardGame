package com.example.cardgame.widget
import android.content.Context
import android.util.AttributeSet
import android.widget.TableRow
import com.example.cardgame.R

class CustomTableHead : TableRow {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    init{
        inflate(context, R.layout.custom_table_head,this)
    }
}
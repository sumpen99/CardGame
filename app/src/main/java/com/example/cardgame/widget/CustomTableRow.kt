package com.example.cardgame.widget
import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.cardgame.R

class CustomTableRow(context: Context?, attrs: AttributeSet?,var index:String,var name:String,var score:String) : TableRow(context,attrs){

    init{
        inflate(context, R.layout.custom_table_row,this)
        setColumns("1","Fredrik Sundström","999")
    }

    fun setColumns(index:String,name:String,score:String){
        val rowLayout = getChildAt(0) as LinearLayout
        val rowIndex:TextView = rowLayout.getChildAt(0) as TextView
        val rowName:TextView =  rowLayout.getChildAt(1) as TextView
        val rowScore:TextView = rowLayout.getChildAt(2) as TextView
        rowIndex.text = index
        rowName.text = name
        rowScore.text = score
    }


}
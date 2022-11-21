package com.example.cardgame.struct
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
//https://www.digitalocean.com/community/tutorials/android-alert-dialog-using-kotlin
class MessageToUser(val context:Context,val view: View?,val message:String){
    init{ showMessage()}

    private fun showMessage(){
        val positiveButtonClick = {dialog:DialogInterface,which:Int->
            Toast.makeText(context,"OK Gotcha!",Toast.LENGTH_SHORT).show()
        }
        val negativeButtonClick = {dialog:DialogInterface,which:Int->
            Toast.makeText(context,"That's Alright!",Toast.LENGTH_SHORT).show()
        }
        val neutralButtonClick = {dialog:DialogInterface,which:Int->
            Toast.makeText(context,"Maybe",Toast.LENGTH_SHORT).show()
        }
        val builder:AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(message)
        //builder.setMessage(message)
        builder.setPositiveButton("OK",DialogInterface.OnClickListener(positiveButtonClick))
        builder.setNegativeButton("NO",DialogInterface.OnClickListener(negativeButtonClick))
        //builder.setNeutralButton("Maybe",DialogInterface.OnClickListener(neutralButtonClick))
        builder.show()
    }
}
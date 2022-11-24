package com.example.cardgame.struct
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View

class MessageToUser(val context:Context,val view: View?,val args:Any?,val callbackYes:(args:Any?)->Unit,val callbackNo:(args:Any?)->Unit,val message:String){
    init{ showMessage()}

    private fun showMessage(){
        val builder:AlertDialog.Builder = AlertDialog.Builder(context)
        val positiveButtonClick = { dialog:DialogInterface,which:Int->
            callbackYes(args)
        }
        val negativeButtonClick = {dialog:DialogInterface,which:Int->
            callbackNo(args)
            dialog.cancel()
        }
        /*val neutralButtonClick = {dialog:DialogInterface,which:Int->
            Toast.makeText(context,"Maybe",Toast.LENGTH_SHORT).show()
        }
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        MainActivity.this.finish();
                    }
                  })
        */
        builder.setTitle(message)
        //builder.setMessage(message)
        builder.setPositiveButton("YES",DialogInterface.OnClickListener(positiveButtonClick))
        builder.setNegativeButton("NO",DialogInterface.OnClickListener(negativeButtonClick))
        //builder.setNeutralButton("Maybe",DialogInterface.OnClickListener(neutralButtonClick))
        builder.show().setCanceledOnTouchOutside(false)
    }
}
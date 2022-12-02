package com.example.cardgame.struct
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View

class MessageToUser(val context:Context,val view: View?){

    private var positiveCallback:((args:Any?)->Unit) ? = null
    private var negativeCallback:((args:Any?)->Unit) ? = null
    private var callbackArgs:Any? = null
    private var message:String = ""
    private var isOpen:Boolean = false

    private fun setCallbackArgs(args:Any?){
        callbackArgs = args
    }

    fun setPositiveCallback(callback:(args:Any?)->Unit){
        positiveCallback = callback
    }

    fun setNegativeCallback(callback:(args:Any?)->Unit){
        negativeCallback = callback
    }

    fun setMessage(msg:String){
        message = msg
    }

    private fun setStatusOpen(value:Boolean){
        isOpen = value
    }

    fun showMessage(){
        if(!isOpen){
            setStatusOpen(true)
            val builder:AlertDialog.Builder = AlertDialog.Builder(context)
            val positiveButtonClick = { dialog:DialogInterface,which:Int->
                if(positiveCallback!=null){ positiveCallback!!(callbackArgs)}
                setStatusOpen(false)
            }
            val negativeButtonClick = {dialog:DialogInterface,which:Int->
                if(negativeCallback!=null){negativeCallback!!(callbackArgs)}
                setStatusOpen(false)
                //dialog.cancel()
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
}
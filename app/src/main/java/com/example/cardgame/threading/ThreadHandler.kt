package com.example.cardgame.threading
import com.example.cardgame.interfaces.IThreading
import com.example.cardgame.io.printToTerminal

private var listOfTBF:HashMap<Long,TBF> = hashMapOf()

fun executeNewThread(obj: IThreading) {
    if(obj.getCallbackStatus()){ return}
    val tbf = TBF(obj)
    val t = Thread(tbf)
    tbf.myThreadId = t.id
    listOfTBF[t.id] = tbf
    t.start()
}

fun closeAndRemoveAllThreads(){
    for(obj in listOfTBF.iterator()){
        obj.value.stopThreadActivity()
        removeThread(obj.value.myThreadId)
    }
}

fun closeAllThreads(threadId:Long){
    for(obj in listOfTBF.iterator()){
        obj.value.stopThreadActivity()
    }
}

fun removeThread(threadId:Long){
    val tbf:TBF? = listOfTBF[threadId]
    if(tbf!=null){
        listOfTBF.remove(threadId)
    }
}

class TBF(var threadObject:IThreading) : Runnable {
    var myThreadId:Long = 0
    override fun run() {
        threadObject.setCallbackStatus(true)
        threadObject.startActivity()
        removeThread(myThreadId)
        printToTerminal("Thread Is Done")
    }

    fun stopThreadActivity(){
        threadObject.stopActivity()
        removeThread(myThreadId)
    }

}
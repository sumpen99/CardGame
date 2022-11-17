package com.example.cardgame.board
import com.example.cardgame.struct.BoardCell

class GameBoard(private var rows:Int,private var columns:Int) {
    private var size : Int = 0
    private var m: Array<BoardCell>

    init{
        size = rows*columns
        m = Array(size){BoardCell()}
    }

    fun getValue(row: Int, col: Int): BoardCell {
        return m[getIndex(row, col)]
    }

    fun setValue(row: Int, col: Int, value: BoardCell) {
        m[getIndex(row, col)] = value
    }

    fun getIndex(row: Int, col: Int): Int {
        return row * columns + col
    }

    fun flipVertical() {
        var c: Int
        var r = 0
        var j: Int
        while (r < rows / 2) {
            c = rows - 1 - r
            j = 0
            while (j < columns) {
                val temp = m[r * columns + j]
                m[r * columns + j] = m[c * columns + j]
                m[c * columns + j] = temp
                j++
            }
            r++
        }
    }

    fun getColFromIndex(index: Int): Int {
        return index % columns
    }

    fun getRowFromIndex(index: Int): Int {
        return index / columns
    }
}
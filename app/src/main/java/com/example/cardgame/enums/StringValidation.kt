package com.example.cardgame.enums

enum class StringValidation(val message:String) {
    STRING_IS_EMPTY("Empty Field"),
    STRING_CONTAINS_ILLEGAL_CHAR("Illegal Char Detected"),
    STRING_IS_TOO_LONG("Field Is Too Long"),
    STRING_IS_OK("OK")
}
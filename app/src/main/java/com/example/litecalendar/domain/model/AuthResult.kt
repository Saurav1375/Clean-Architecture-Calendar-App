package com.example.litecalendar.domain.model

sealed class AuthResult(val message : String? = null) {
    data object Success : AuthResult()
    class Error(message : String) : AuthResult(message)
    data class Loading(val isLoading : Boolean = true) : AuthResult()
}
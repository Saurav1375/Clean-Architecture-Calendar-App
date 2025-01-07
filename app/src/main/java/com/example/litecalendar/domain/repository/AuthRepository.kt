package com.example.litecalendar.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.example.litecalendar.data.auth.SignInResult
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.domain.model.AuthResult

interface AuthRepository {
    suspend fun SignIn(): IntentSender?
    suspend fun SignInWithIntent(intent: Intent): SignInResult
    suspend fun signOut()
    fun getSignedInUser(): UserData?
}
package com.example.litecalendar.data.auth


data class UserData(
    val userId: String,
    val username : String?,
    val profilePictureUrl : String?,
    val emailId: String = ""

)
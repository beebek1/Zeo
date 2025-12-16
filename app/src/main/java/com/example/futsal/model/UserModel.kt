package com.example.futsal.model

data class UserModel(
    var userId: String ="",
    var email: String ="",
    var firstName: String ="",
    var lastName: String ="",
    var dob: String ="",
    var gender: String =""
){
    fun toMap() : Map<String,Any?>{
        return mapOf(
            "userId" to userId,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "dob" to dob,
            "gender" to gender

        )
    }
}




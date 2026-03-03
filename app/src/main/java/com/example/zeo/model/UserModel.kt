package com.example.zeo.model

data class UserModel(
    var userId: String = "",
    var email: String = "",
    var fullName: String = "",
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "email" to email,
            "fullName" to fullName // Corrected key to match property name
        )
    }
}

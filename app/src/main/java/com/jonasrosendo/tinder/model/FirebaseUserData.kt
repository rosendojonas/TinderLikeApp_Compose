package com.jonasrosendo.tinder.model

data class FirebaseUserData(
    val userId: String? = "",
    val name: String? = "",
    val username: String? = "",
    val imageUrl: String? = "",
    val bio: String? = "",
    val gender: String? = "",
    val genderPreference: String? = "",
    var swipesLeft: List<String> = listOf(),
    var swipesRight: List<String> = listOf(),
    var matches: List<String> = listOf()
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "name" to name,
        "username" to username,
        "imageUrl" to imageUrl,
        "bio" to bio,
        "gender" to gender,
        "genderPreference" to genderPreference,
        "swipesLeft" to swipesLeft,
        "swipesRight" to swipesRight,
        "matches" to matches
    )
}
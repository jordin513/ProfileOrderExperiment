package com.nidroj.profileorderexperiment.data.model

import com.google.gson.annotations.SerializedName


class User {
    @SerializedName("id")
    var id = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("about")
    var about: String? = null

    @SerializedName("photo")
    var photo: String? = null

    @SerializedName("school")
    var school: String? = null

    @SerializedName("hobbies")
    var hobbies: List<String>? = null
}
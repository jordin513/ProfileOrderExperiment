package com.nidroj.profileorderexperiment.data.model

import com.google.gson.annotations.SerializedName

open class UsersResponse {
    @SerializedName("users")
    var users: List<User>? = null
}
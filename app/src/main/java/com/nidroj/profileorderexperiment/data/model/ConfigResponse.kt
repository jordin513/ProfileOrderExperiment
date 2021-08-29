package com.nidroj.profileorderexperiment.data.model

import com.google.gson.annotations.SerializedName

open class ConfigResponse {
    @SerializedName("profile")
    var profile: List<String>? = null
}
package com.nidroj.profileorderexperiment.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matched_users")
class MatchedUser() {

    constructor(user: User, config: String, profileEngagementTime: Long?) : this() {
        id = user.id
        name = user.name
        gender = user.gender
        about = user.about
        photo = user.photo
        school = user.school
        this.config = config
        this.profileEngagementTime = profileEngagementTime

        user.hobbies?.let { hobbies ->
            this.hobbies = hobbies.joinToString { it }
        }
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "gender")
    var gender: String? = null

    @ColumnInfo(name = "about")
    var about: String? = null

    @ColumnInfo(name = "photo")
    var photo: String? = null

    @ColumnInfo(name = "school")
    var school: String? = null

    @ColumnInfo(name = "hobbies")
    var hobbies: String? = null

    @ColumnInfo(name = "config")
    var config: String? = null

    @ColumnInfo(name = "profile_engagement_time")
    var profileEngagementTime: Long? = null
}
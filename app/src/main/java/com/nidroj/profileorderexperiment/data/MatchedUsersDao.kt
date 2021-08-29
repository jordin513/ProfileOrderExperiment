package com.nidroj.profileorderexperiment.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nidroj.profileorderexperiment.data.model.MatchedUser

@Dao
interface MatchedUsersDao {
    @get:Query("SELECT * FROM matched_users")
    val all: LiveData<List<MatchedUser>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: MatchedUser)

    @Query("SELECT COUNT(*) FROM matched_users WHERE photo is not null")
    fun getPhotoCount(): LiveData<Int?>

    @Query("SELECT school FROM matched_users GROUP BY school ORDER BY COUNT(school) DESC LIMIT 1")
    fun getTopSchool(): LiveData<String?>

    @Query("SELECT profile_engagement_time FROM matched_users ORDER BY profile_engagement_time DESC LIMIT 1")
    fun getLongestInteractionTime(): LiveData<Long?>
}
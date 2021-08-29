package com.nidroj.profileorderexperiment.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nidroj.profileorderexperiment.data.UserRepository
import com.nidroj.profileorderexperiment.data.model.MatchedUser

class MatchesViewModel() : ViewModel() {

    //gets all matches in database
    fun getMatches(context: Context): LiveData<List<MatchedUser>?> {
        return UserRepository.getMatches(context)
    }

    //gets the amount of matched users that have a photo
    fun getPhotoCount(context: Context): LiveData<Int?> {
        return UserRepository.getPhotoCount(context)
    }

    /* Gets the most common school.
    * NOTE: if there is more than one school with the highest count,
    * only one will be shown.
    */
    fun getTopSchool(context: Context): LiveData<String?> {
        return UserRepository.getTopSchool(context)
    }
}
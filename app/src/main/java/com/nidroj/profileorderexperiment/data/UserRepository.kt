package com.nidroj.profileorderexperiment.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.nidroj.profileorderexperiment.api.HingeApi
import com.nidroj.profileorderexperiment.data.model.ConfigResponse
import com.nidroj.profileorderexperiment.data.model.MatchedUser
import com.nidroj.profileorderexperiment.data.model.UsersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class UserRepository(private val api: HingeApi) {

    //attempts to call users endpoint
    suspend fun getUsers(): Response<UsersResponse>? {
        return kotlin.runCatching {
            api.getUsers()
        }.getOrElse { t ->
            Timber.e("getUsers(): error getting users, ${t.message}")
            null
        }
    }

    //attempts to call config endpoint
    suspend fun getConfig(): Response<ConfigResponse>? {
        return kotlin.runCatching {
            api.getConfig()
        }.getOrElse { t ->
            Timber.e("getConfig(): error getting config, ${t.message}")
            null
        }
    }

    /* I have included a few example queries that would be performed on the data.
    * They are completely arbitrary for demonstration purposes. */
    companion object {
        lateinit var db: MatchedUsersDatabase

        private fun initializeDB(context: Context): MatchedUsersDatabase {
            return MatchedUsersDatabase.createDb(context)
        }

        //inserts match into database
        fun insertMatch(context: Context, match: MatchedUser) {
            db = initializeDB(context)

            CoroutineScope(Dispatchers.IO).launch {
                db.matchUserDao().insert(match)
            }
        }

        //deletes all matches in database
        fun deleteAllMatches(context: Context) {
            db = initializeDB(context)

            CoroutineScope(Dispatchers.IO).launch {
                db.clearAllTables()
            }
        }

        //gets all matches in database
        fun getMatches(context: Context): LiveData<List<MatchedUser>?> {
            db = initializeDB(context)

            return db.matchUserDao().all
        }

        //gets the amount of matched users that have a photo
        fun getPhotoCount(context: Context): LiveData<Int?> {
            db = initializeDB(context)

            return db.matchUserDao().getPhotoCount()
        }

        /* Gets the most common school.
        * NOTE: if there is more than one school with the highest count,
        * only one will be shown.*/
        fun getTopSchool(context: Context): LiveData<String?> {
            db = initializeDB(context)

            return db.matchUserDao().getTopSchool()
        }

        // Gets the longest amount of time spent the user spent looking at a profile
        fun getLongestInteractionTime(context: Context): LiveData<Long?> {
            db = initializeDB(context)

            return db.matchUserDao().getLongestInteractionTime()
        }
    }
}
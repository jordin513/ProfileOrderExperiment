package com.nidroj.profileorderexperiment.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nidroj.profileorderexperiment.data.model.MatchedUser
import timber.log.Timber

@Database(entities = [MatchedUser::class], version = 1)
abstract class MatchedUsersDatabase : RoomDatabase() {
    abstract fun matchUserDao(): MatchedUsersDao

    companion object {

        @Volatile
        private var INSTANCE: MatchedUsersDatabase? = null
        fun createDb(context: Context): MatchedUsersDatabase {

            INSTANCE?.let {
                Timber.d("Returning existing database instance")

                return it
            } ?: synchronized(this) {
                Timber.d("Returning new database instance")

                INSTANCE = Room
                    .databaseBuilder(context, MatchedUsersDatabase::class.java, "matches_db")
                    .fallbackToDestructiveMigration()
                    .build()

                return INSTANCE!!

            }
        }
    }
}
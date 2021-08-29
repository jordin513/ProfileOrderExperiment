package com.nidroj.profileorderexperiment.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidroj.profileorderexperiment.data.UserRepository
import com.nidroj.profileorderexperiment.data.model.MatchedUser
import com.nidroj.profileorderexperiment.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


/* NOTE: Users could be stored in a database, but typically with dating apps,
* they are not stored because your available users change often.
*/
class UsersViewModel(private val userRepository: UserRepository) : ViewModel() {

    val users = MutableLiveData<List<User>>()
    val config = MutableLiveData<List<String>>()
    val errorMessage = MutableLiveData<String>()

    init {
        callApi()
    }

    //gets users and config data from api
    fun callApi() {
        /* hits both api endpoints. Ensures that both are called in order
        * to avoid race conditions.
        */
        viewModelScope.launch {
            withContext(coroutineContext) { fetchConfig() }
            withContext(coroutineContext) { fetchUsers() }
        }
    }

    //gets user data
    private suspend fun fetchUsers() {
        Timber.d("fetchUsers(): attempting to get users.")

        var usersResponse = emptyList<User>()
        val response = userRepository.getUsers()
        withContext(Dispatchers.Main) {
            response?.let {
                Timber.d("fetchUsers(): Response code: ${response.code()}")
                if (response.isSuccessful) {
                    Timber.d("fetchUsers(): Successfully got a response users.")

                    // if there are users in the response
                    response.body()?.users?.let {
                        Timber.d("fetchUsers(): Successfully got users from response. Size: ${it.size}")

                        usersResponse = it
                    } ?: errorMessage.postValue("No users from api to show")
                    // not necessarily an error, may just be no one to show

                } else {
                    //Call was not successful
                    errorMessage.postValue("Users call was unsuccessful: ${response.message()}")
                }
            }
        } ?: errorMessage.postValue("Error getting users")

        users.postValue(usersResponse)
    }

    //gets config data
    private suspend fun fetchConfig() {
        Timber.d("fetchConfig(): attempting to get users.")

        val response = userRepository.getConfig()
        withContext(Dispatchers.Main) {
            response?.let {
                Timber.d("fetchConfig(): Response code: ${response.code()}")

                if (response.isSuccessful) {
                    Timber.d("fetchConfig(): Successfully got a response users.")

                    response.body()?.profile?.let { config ->
                        Timber.d("fetchConfig(): Successfully got config from response.")

                        this@UsersViewModel.config.postValue(config)

                    } ?: errorMessage.postValue("Error getting config")
                } else {
                    //Call was not successful
                    errorMessage.postValue("Config call was unsuccessful: ${response.message()}")
                }
            }
        }
    }

    //inserts match into database
    fun insertMatch(context: Context, match: MatchedUser) {
        UserRepository.insertMatch(context, match)
    }

    //deletes all matches in database
    fun deleteAllMatches(context: Context) {
        UserRepository.deleteAllMatches(context)
    }

}



package com.nidroj.profileorderexperiment.ui.view

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.nidroj.profileorderexperiment.R
import com.nidroj.profileorderexperiment.api.HingeApi
import com.nidroj.profileorderexperiment.data.UserRepository
import com.nidroj.profileorderexperiment.data.model.MatchedUser
import com.nidroj.profileorderexperiment.data.model.User
import com.nidroj.profileorderexperiment.databinding.ActivityMainBinding
import com.nidroj.profileorderexperiment.ui.adapter.ProfileAdapter
import com.nidroj.profileorderexperiment.ui.viewmodel.UsersViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var currentUser: User? = null

    private val hingeViewModel = UsersViewModel(UserRepository(HingeApi.createApi()))
    private val gson = Gson()

    //set a default value in case a there is an issue getting a response
    private lateinit var config: List<String>
    private lateinit var binding: ActivityMainBinding
    private lateinit var userStack: Stack<User>
    private var startProfileEngagement: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.hinge)

        config = resources.getStringArray(R.array.default_config).toList()

        hingeViewModel.users.observe(this, { users ->
            Timber.d("Got users from ViewModel. Size: ${users.size}")
            showUsers()

            //inverse the list to be in the same order as the response
            userStack = Stack<User>().also { it.addAll(users.reversed()) }
            updateUser()
        })

        hingeViewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            noUsersToShow()
        })

        hingeViewModel.config.observe(this, { config ->
            Timber.d("Overriding default config.")
            this.config = config
        })

        binding.match.setOnClickListener {
            Timber.d("Matched with user.")

            /* As a bonus, I decided to log the total amount of time a user
            * spent looking at a profile. This is useful to analyze too.*/
            var profileEngagementTime: Long? = null
            startProfileEngagement?.let { start ->
                profileEngagementTime = System.currentTimeMillis() - start
            }

            /* For demonstration purposes, I am adding all matches to an
            * on-device database. In production, this data can be stored
            * wherever you wish (local server db, cloud database, etc.)*/

            val match = MatchedUser(currentUser!!, gson.toJson(config), profileEngagementTime)
            hingeViewModel.insertMatch(this, match)

            updateUser()
        }

        binding.next.setOnClickListener {
            Timber.d("Skipping user, no match.")
            updateUser()

            /* NOTE: Here you would notify that the user is not a match,
            * so that they wont be shown again.
            * */
        }

        //Opens activity that shows your match data
        binding.seeData.setOnClickListener {
            startActivity(Intent(this, MatchDataActivity::class.java))
        }

        //Deletes all database data and resets users
        binding.resetData.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            hingeViewModel.deleteAllMatches(this)
            hingeViewModel.callApi()
        }
    }

    //updates the user being shown. If there are no users to show, screen will update.
    private fun updateUser() {
        Timber.d("updateUser(): Attempting to update shown user: ${userStack.size}")

        // there are users to show
        if (userStack.isNotEmpty()) {
            val items: ArrayList<ProfileAdapter.ProfileSection> = ArrayList()
            currentUser = userStack.pop()
            Timber.d("updateUser(): User found: ${currentUser!!.id}")

            //iterates through config order and creates adapter items from currentUser's data
            for (section: String in config) {
                try {
                    val sectionValue = JSONObject(gson.toJson(currentUser)).get(section)

                    val item: ProfileAdapter.ProfileSection? = getSectionItem(section, sectionValue)
                    item?.let {
                        items.add(it)
                    }

                } catch (e: JSONException) {
                    // User did not have this section.
                    continue

                    /* NOTE: additional checking could be added to ensure that
                    * id, name, and gender aren't null like the prompt says.
                    */
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing user data${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            //smooth transition to next user
            TransitionManager.beginDelayedTransition(binding.recyclerView)

            //update adapter with new user's info
            binding.recyclerView.adapter = ProfileAdapter(this).apply { this.items = items }

            //set start time for user engagement
            startProfileEngagement = System.currentTimeMillis()

        } else {
            Timber.d("updateUser(): No user to show.")

            currentUser = null
            noUsersToShow()
        }
    }

    //creates an adapter item for ta given section
    private fun getSectionItem(section: String, sectionValue: Any): ProfileAdapter.ProfileSection? {
        Timber.d("getSectionItem(): Creating section for: $section")

        when (section) {
            "id" -> {
                Timber.d("getSectionItem(): Handling ID section.")
                return null
            }
            getString(R.string.photo) -> {
                Timber.d("getSectionItem(): Handling photo section.")
                return ProfileAdapter.ImageItem(sectionValue.toString())

            }
            getString(R.string.name) -> {
                Timber.d("getSectionItem(): Handling name section.")

                //Name section doesn't get a subtitle
                return ProfileAdapter.InfoItem(sectionValue.toString())

            }
            getString(R.string.hobbies) -> {
                Timber.d("getSectionItem(): Handling hobbies section.")

                //hobbies returned as list gets converted into a comma separated list
                val hobbies: String = (sectionValue as JSONArray)
                    .join(", ")
                    .replace("\"", "")   // gets rid of quotation marks

                return ProfileAdapter.InfoItem(section, hobbies)
            }
            getString(R.string.gender) -> {
                Timber.d("getSectionItem(): Handling gender section.")

                //sets value to uppercase
                return ProfileAdapter.InfoItem(section, sectionValue.toString().uppercase())

            }
            else -> {

                Timber.d("getSectionItem(): Handling a basic (Header and String value) section.")
                return ProfileAdapter.InfoItem(section, sectionValue.toString())
            }
        }
    }

    //sets view to a neutral state when there are no user to show
    private fun noUsersToShow() {
        binding.next.visibility = View.GONE
        binding.match.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.noUsers.visibility = View.VISIBLE
    }

    //sets view show user profiles and matching buttons
    private fun showUsers() {
        binding.progressBar.visibility = View.GONE
        binding.next.visibility = View.VISIBLE
        binding.match.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.noUsers.visibility = View.GONE
    }

}
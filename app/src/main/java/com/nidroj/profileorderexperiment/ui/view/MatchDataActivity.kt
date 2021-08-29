package com.nidroj.profileorderexperiment.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nidroj.profileorderexperiment.R
import com.nidroj.profileorderexperiment.databinding.ActivityMatchDataBinding
import com.nidroj.profileorderexperiment.ui.viewmodel.MatchesViewModel
import java.util.concurrent.TimeUnit

class MatchDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMatchDataBinding
    private val matchesViewModel = MatchesViewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.match_data)

        matchesViewModel.getPhotoCount(this).observe(this, { count ->

            count?.let {
                //Sets the textview to number of matches that had images.
                binding.imageCount.text = count.toString()
            } ?: noMatches()
        })

        matchesViewModel.getTopSchool(this).observe(this, { school ->
            school?.let {
                /* Sets the textview to the most common school.
                * NOTE: if there is more than one school with the highest count,
                * only one will be shown.
                */

                binding.topSchool.text = school.toString()
            } ?: noTopSchool()
        })

        matchesViewModel.getLongestInteractionTime(this).observe(this, { time ->
            time?.let {

                // Gets the longest amount of time spent the user spent looking at a profile
                binding.time.text = "${TimeUnit.MILLISECONDS.toSeconds(time)} seconds"
            } ?: noMatches()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    //Shows textview stating there were no rows in the database.
    private fun noMatches() {
        binding.noUsers.visibility = View.VISIBLE
        binding.data.visibility = View.GONE
    }

    /* If there are no matches, or no matches had a school,
    * the value will be "none".
    */
    private fun noTopSchool() {
        binding.topSchool.text = getString(R.string.none)
    }
}
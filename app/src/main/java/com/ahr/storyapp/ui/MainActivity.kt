package com.ahr.storyapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.ActivityMainBinding
import com.ahr.storyapp.ui.addstory.AddStoryActivity
import com.ahr.storyapp.ui.auth.AuthActivity
import com.ahr.storyapp.ui.auth.AuthViewModel
import com.ahr.storyapp.ui.home.HomeFragment
import com.ahr.storyapp.ui.maps.MapsActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    private var token: String? = null

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        authViewModel.getAuthToken()

        setupToolbar()
        setupObserver()

        observeAuthToken()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_location -> navigateToMapsScreen()
                R.id.action_logout -> logout()
            }
            true
        }
    }

    private fun setupHomeFragment(token: String) {
        supportFragmentManager.commit {
            val bundle = bundleOf(EXTRA_TOKEN to token)
            setReorderingAllowed(true)
            replace<HomeFragment>(R.id.fragment_container, args = bundle)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupObserver() {
        binding.fabAddNewStory.setOnClickListener { navigateToAddNewStoryScreen() }
    }

    private fun observeAuthToken() {
        authViewModel.authToken.observe(this) { authToken ->
            if (authToken.isEmpty()) {
                navigateToAuthScreen()
                return@observe
            }
            token = authToken
            setupHomeFragment(authToken)
        }
    }

    private fun navigateToAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun navigateToAddNewStoryScreen() {
        val intent = Intent(this, AddStoryActivity::class.java).apply {
                putExtra(AddStoryActivity.EXTRA_TOKEN, token)
            }
        startActivity(intent)
    }

    private fun navigateToMapsScreen() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_message)
            .setPositiveButton(R.string.yes) { _, _ -> authViewModel.removeAuthToken() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }
}
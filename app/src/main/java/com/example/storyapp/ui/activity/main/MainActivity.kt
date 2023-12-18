package com.example.storyapp.ui.activity.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.activity.ViewModelFactory
import com.example.storyapp.ui.activity.addStory.AddStoryActivity
import com.example.storyapp.ui.activity.map.MapsActivity
import com.example.storyapp.ui.activity.welcome.WelcomeActivity
import com.example.storyapp.ui.adapter.AdapterUserStory
import com.example.storyapp.ui.adapter.LoadingStateAdapter

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var token: String
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupView()
        setupAction()
        viewModelObserve()

        setReviewData()
    }

    private fun setupUI() {
        binding.pbProgressBar.visibility = View.GONE
        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryUsers.layoutManager = layoutManager
        binding.rvStoryUsers.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    private fun viewModelObserve() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                navigateTo("WelcomeActivity")
            } else {
                user.token.let {
                    token = it
                    binding.fabAddStory.setOnClickListener {
                        navigateTo("AddStoryActivity")
                    }

                    val userWelcome = resources.getString(R.string.userWelcome) + user.name + "!"
                    binding.nameTextView.text = userWelcome

                    if (!::token.isInitialized) {
                        token = it
                    }
                }
            }
        }
        viewModel.isLoading.observe(this) {loading ->
            showLoading(loading)
        }
    }

    private fun navigateTo(activityName: String) {
        when (activityName) {
            "WelcomeActivity" -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            "AddStoryActivity" -> {
                startActivity(Intent(this, AddStoryActivity::class.java))
            }
            else -> {
                showToast(resources.getString(R.string.toastNavigateError))
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.nameTextView.text = viewModel.getSession().value?.name

        binding.settingButton.setOnClickListener {
            if (binding.logoutButton.isVisible) {
                binding.logoutButton.visibility = View.GONE
                binding.languageButton.visibility = View.GONE
                binding.mapButton.visibility = View.GONE
            } else {
                binding.logoutButton.visibility = View.VISIBLE
                binding.languageButton.visibility = View.VISIBLE
                binding.mapButton.visibility = View.VISIBLE
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

        binding.languageButton.setOnClickListener {
            switchLanguage()
        }

        binding.mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun switchLanguage() {
        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
    }

    private fun setReviewData() {

        val adapter = AdapterUserStory()
        binding.rvStoryUsers.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
package com.example.storyapp.ui.activity.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityWelcomeBinding
import com.example.storyapp.ui.activity.login.LoginActivity
import com.example.storyapp.ui.activity.signup.SignupActivity

@Suppress("DEPRECATION")
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupActions()
        playAnimations()
    }

    private fun setupView() {
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun setupActions() {
        binding.loginButton.setOnClickListener { startNewActivity(LoginActivity::class.java) }
        binding.signupButton.setOnClickListener { startNewActivity(SignupActivity::class.java) }
    }

    private fun startNewActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun playAnimations() {
        val logoAnimator = ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val buttonsAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000),
                ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(1000)
            )
        }

        val textAnimator = AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(1000),
                ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(1000),
                buttonsAnimator
            )
        }

        AnimatorSet().apply {
            playTogether(logoAnimator, textAnimator)
            start()
        }
    }
}
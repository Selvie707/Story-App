package com.example.storyapp.ui.activity.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pbProgressBar.visibility = View.GONE

        setupUI()
        playAnimation()
        setUpViewModel()
        setUpTextWatcher()
        setMyButtonEnable()
        signUpButtonOnClick()

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.pbProgressBar.visibility = View.VISIBLE
            } else {
                binding.pbProgressBar.visibility = View.GONE
            }
        }
    }

    private fun signUpButtonOnClick() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(name, email, password,
                {
                    showRegistrationSuccessDialog()
                },
                { errorMessage ->
                    showRegistrationErrorDialog(errorMessage)
                }
            )
        }
    }

    private fun setUpTextWatcher() {
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]
    }

    private fun setMyButtonEnable() {
        val result = binding.passwordEditText.text
        binding.signupButton.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun setupUI() {
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val etName = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, etName, etEmail, etPassword, signup)
            start()
        }
    }

    private fun showRegistrationSuccessDialog() {
        if (!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this).apply {
                val dialogMessage = resources.getString(R.string.welcome) + ", ${binding.emailEditText.text}!"

                setTitle(resources.getString(R.string.dialogRegisterSuccess))
                setMessage(dialogMessage)
                setPositiveButton(resources.getString(R.string.oke)) { _, _ ->
                    finish()
                }
                create().show()
            }
        }
    }

    private fun showRegistrationErrorDialog(message: String) {
        if (!isFinishing && !isDestroyed) {
            AlertDialog.Builder(this).apply {
                setTitle(resources.getString(R.string.dialogRegisterFailed))
                setMessage(message)
                setPositiveButton(resources.getString(R.string.oke), null)
                create().show()
            }
        }
    }
}
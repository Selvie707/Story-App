package com.example.storyapp.ui.activity.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        const val USER_ID = "id"
        const val USERNAME = "username"
        const val DESCRIPTION = "description"
        const val PICTURE = "picture"

        var idUser: String = ""
        var username: String = ""
        var description: String? = null
        var picture: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUser = intent.getStringExtra(USER_ID) ?: ""
        username = intent.getStringExtra(USERNAME) ?: ""
        description = intent.getStringExtra(DESCRIPTION) ?: ""
        picture = intent.getStringExtra(PICTURE) ?: ""

        binding.tvStoryAuthor.text = username
        binding.tvStoryDescription.text = description

        Glide.with(this)
            .load(picture)
            .into(binding.ivStoryImage)
    }
}
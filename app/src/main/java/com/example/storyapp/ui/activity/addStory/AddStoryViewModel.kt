package com.example.storyapp.ui.activity.addStory

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.repository.UserRepository
import java.io.File

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {
    fun uploadStory(image: File, description: String, lat: Double? = null, lon: Double? = null) = repository.upload(image, description, lat, lon)
}
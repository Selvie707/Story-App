package com.example.storyapp.ui.activity.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.pref.Result
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun getStoriesLocation() {
        viewModelScope.launch {
            when (val result = repository.getStoriesLocation()) {
                is Result.Success -> {
                    _storyList.value = result.data!!
                }
                is Result.Failed -> {
                    Log.e(TAG, "onFailure")
                }
                else -> {
                }
            }
        }
    }
}
package com.example.storyapp.ui.activity.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.pref.Result
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val storyList: LiveData<List<ListStoryItem>> = _storyList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStoriesLocation() {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getStoriesLocation()) {
                is Result.Success -> {
                    _storyList.value = result.data!!
                    _isLoading.value = false
                }
                is Result.Failed -> {
                    Log.e(TAG, "onFailure")
                    _isLoading.value = false
                }
                else -> {
                    _isLoading.value = false
                }
            }
        }
    }

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getUserStories().cachedIn(viewModelScope)
}
package com.example.storyapp.ui.activity.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.UserAccountRepository
import com.example.storyapp.data.retrofit.ApiServiceFactory
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserAccountRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    constructor() : this(UserAccountRepository(ApiServiceFactory.apiService))

    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)

                if (response.error == false) {
                    onSuccess.invoke()
                    isLoading.value = false
                } else {
                    onError.invoke("Registration failed: ${response.message}")
                    isLoading.value = false
                }
            } catch (e: Exception) {
                onError.invoke("Registration failed: ${e.message}")
                isLoading.value = false
            }
        }
    }
}
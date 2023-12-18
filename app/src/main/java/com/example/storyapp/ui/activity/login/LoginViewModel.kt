package com.example.storyapp.ui.activity.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val preferenceRepository: UserRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = preferenceRepository.login(email, password)
                if (response.error == false && response.loginResult != null) {
                    val loginResult = response.loginResult

                    val userModel = UserModel(
                        userId = loginResult.userId ?: "",
                        name = loginResult.name ?: "",
                        email = email,
                        token = loginResult.token ?: "",
                        isLogin = true
                    )
                    preferenceRepository.saveSession(userModel)
                    onSuccess.invoke()
                    isLoading.value = false
                } else {
                    onError.invoke("Login failed: ${response.message ?: "Unknown error"}")
                    isLoading.value = false
                }
            } catch (e: Exception) {
                onError.invoke("Login failed: ${e.message ?: "Unknown error"}")
                isLoading.value = false
            }
        }
    }
}
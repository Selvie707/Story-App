package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.repository.UserRepository
import com.example.storyapp.data.retrofit.ApiService
import com.example.storyapp.data.retrofit.ApiServiceFactory

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService: ApiService = ApiServiceFactory.apiService
        return UserRepository.getInstance(pref, apiService)
    }
}
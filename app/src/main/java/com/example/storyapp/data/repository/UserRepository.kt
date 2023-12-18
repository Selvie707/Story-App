package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.pref.Result
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.response.FileUploadResponse
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.data.response.LoginResponse
import com.example.storyapp.data.retrofit.ApiService
import com.example.storyapp.data.retrofit.ApiServiceFactory
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    private val token: Flow<String> = userPreference.getSession().map { it.token }
    private val tokenLiveData: LiveData<String> = token.asLiveData()

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getUserStories(): LiveData<PagingData<ListStoryItem>> {
        return tokenLiveData.switchMap {
            Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                pagingSourceFactory = {
                    StoryPagingSource(it)
                }
            ).liveData
        }
    }

    suspend fun getStoriesLocation(): Result<List<ListStoryItem>> {
        return try {
            val apiService = ApiServiceFactory.getApiService(getSession().first().token)
            val response = apiService.getStoriesLocation()

            if (response.error == false) {
                Result.Success(response.listStory)
            } else {
                Result.Failed("onFailure: ${response.message}")
            }
        } catch (e: Exception) {
            Result.Failed("onFailure: ${e.message}")
        }
    }

    fun upload(image: File, description: String, lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val latRequestBody = lat?.toString()?.toRequestBody()
        val lonRequestBody = lon?.toString()?.toRequestBody()

        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile
        )
        try {
            val apiService = ApiServiceFactory.getApiService(getSession().first().token)
            val successResponse = apiService.uploadImage(multipartBody, requestBody, latRequestBody, lonRequestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            emit(Result.Failed(errorResponse.message))
        } catch (e: Exception) {
            emit(Result.Failed(e.message.toString()))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}

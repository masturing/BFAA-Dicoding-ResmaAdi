package com.ran.githubuserapp.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.githubuserapp.database.FavoriteEntity
import com.ran.githubuserapp.datasource.Favorite
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.network.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailUserViewModel(username: String, app: Application) : ViewModel() {
    private val favoriteData: Favorite = Favorite(app)
    private val _detailUser = MutableLiveData<Users?>()
    val detailUser: LiveData<Users?> = _detailUser
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch { getDetailUser(username) }
    }

    fun insert(favEntity: FavoriteEntity) {
        favoriteData.insert(favEntity)
    }

    fun delete(favEntity: FavoriteEntity) {
        favoriteData.delete(favEntity)
    }

    fun getFavoriteById(id: Int): LiveData<List<FavoriteEntity>> {
        return favoriteData.getUserFavoriteById(id)
    }

    private suspend fun getDetailUser(username: String){
        coroutineScope.launch {
            val result = ApiConfig.getApiService().getDetailUserAsync(username)
            _isLoading.value = true
            try {
                _isLoading.value = false
                _detailUser.postValue(result)
            } catch (e: Exception){
                _isLoading.value = false
                _isFailed.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
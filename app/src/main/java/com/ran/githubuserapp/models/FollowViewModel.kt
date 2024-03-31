package com.ran.githubuserapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.network.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowViewModel(username: String) : ViewModel() {
    private val _followers = MutableLiveData<ArrayList<Users>?>()
    val followers: LiveData<ArrayList<Users>?> = _followers
    private val _following = MutableLiveData<ArrayList<Users>?>()
    val following: LiveData<ArrayList<Users>?> = _following
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed: LiveData<Boolean> = _isFailed

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch {
            getListFollowers(username)
            getListFollowing(username)
        }
    }

    private fun getListFollowing(username: String) {
        coroutineScope.launch {
            _isLoading.value = true
            val result = ApiConfig.getApiService().getListFollowing(username)
            try {
                _isLoading.value = false
                _following.postValue(result)
            } catch (e: Exception){
                _isLoading.value = false
                _isFailed.value = true
            }
        }
    }

    private fun getListFollowers(username: String) {
        coroutineScope.launch {
            _isLoading.value = true
            val result = ApiConfig.getApiService().getListFollowers(username)
            try {
                _isLoading.value = false
                _followers.postValue(result)
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
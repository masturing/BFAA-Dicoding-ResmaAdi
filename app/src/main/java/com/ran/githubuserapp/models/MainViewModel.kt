package com.ran.githubuserapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.githubuserapp.datasource.Search
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.network.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _detailUser = MutableLiveData<Users?>()
    val detailUser: LiveData<Users?> = _detailUser
    private val _user = MutableLiveData<ArrayList<Users>?>()
    val user: LiveData<ArrayList<Users>?> = _user
    private val _searchUser = MutableLiveData<ArrayList<Users>?>()
    val searchUser: LiveData<ArrayList<Users>?> = _searchUser
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch { getListUser() }
    }

    private suspend fun getListUser() {
        coroutineScope.launch {
            _isLoading.value = true
            val getUser = ApiConfig.getApiService().getListUsersAsync()
            try {
                _isLoading.value = false
                _user.postValue(getUser)
            } catch (e: Exception){
                _isLoading.value = false
            }
        }
    }

    fun getUserSearch(user: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserBySearch(user)
        client.enqueue(object : Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) {
                if(response.isSuccessful){
                    _isLoading.value = false
                    val responseBody = response.body()
                    if (responseBody != null){
                        if (responseBody.items != null){
                            _searchUser.postValue(responseBody.items)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Search>, t: Throwable) {
                _isLoading.value = false
            }

        })
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
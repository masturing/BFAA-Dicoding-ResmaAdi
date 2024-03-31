package com.ran.githubuserapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FollowFactory(private val username: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FollowViewModel::class.java)){
            return FollowViewModel(username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
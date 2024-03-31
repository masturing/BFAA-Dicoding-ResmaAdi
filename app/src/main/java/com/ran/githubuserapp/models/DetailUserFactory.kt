package com.ran.githubuserapp.models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DetailUserFactory(private val username: String, private val app: Application): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailUserViewModel::class.java)){
            return DetailUserViewModel(username, app) as T
        }
        throw IllegalAccessException("Illegal ViewModel")
    }
}
package com.ran.githubuserapp.models

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavoriteFactory private constructor(private val application: Application) :
    ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: FavoriteFactory? = null

        @JvmStatic
        fun getInstance(application: Application): FavoriteFactory {
            if (INSTANCE == null) {
                synchronized(FavoriteFactory::class.java) {
                    INSTANCE = FavoriteFactory(application)
                }
            }
            return INSTANCE as FavoriteFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class : ${modelClass.name} ")
    }

}
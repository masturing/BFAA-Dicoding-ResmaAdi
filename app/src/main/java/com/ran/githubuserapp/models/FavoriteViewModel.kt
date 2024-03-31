package com.ran.githubuserapp.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ran.githubuserapp.database.FavoriteEntity
import com.ran.githubuserapp.datasource.Favorite

class FavoriteViewModel(application : Application) : ViewModel() {
    private val favoriteData : Favorite = Favorite(application)
    fun getAllFavorites() : LiveData<List<FavoriteEntity>> = favoriteData.getAllFavorites()
}
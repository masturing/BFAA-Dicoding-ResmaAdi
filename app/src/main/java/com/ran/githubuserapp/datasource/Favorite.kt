package com.ran.githubuserapp.datasource

import android.app.Application
import androidx.lifecycle.LiveData
import com.ran.githubuserapp.database.FavoriteDao
import com.ran.githubuserapp.database.FavoriteEntity
import com.ran.githubuserapp.database.FavoriteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Favorite(application: Application) {
    private val favoriteDao: FavoriteDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = FavoriteRoomDatabase.getDatabase(application)
        favoriteDao = db.favoriteDao()
    }

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = favoriteDao.getAllFavorite()
    fun getUserFavoriteById(id: Int): LiveData<List<FavoriteEntity>> =
        favoriteDao.getUserFavoriteById(id)

    fun insert(fav: FavoriteEntity) {
        executorService.execute { favoriteDao.insert(fav) }
    }

    fun delete(fav: FavoriteEntity) {
        executorService.execute { favoriteDao.delete(fav) }
    }
}
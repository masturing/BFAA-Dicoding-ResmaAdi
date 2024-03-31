package com.ran.githubuserapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ran.githubuserapp.R
import com.ran.githubuserapp.adapter.FavoriteAdapter
import com.ran.githubuserapp.database.FavoriteEntity
import com.ran.githubuserapp.databinding.ActivityFavoriteBinding
import com.ran.githubuserapp.models.FavoriteFactory
import com.ran.githubuserapp.models.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private var favorite: ArrayList<FavoriteEntity> = arrayListOf()
    private val adapter: FavoriteAdapter by lazy {
        FavoriteAdapter(favorite)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favoriteViewModel = getViewModel(this@FavoriteActivity)
        showRecyclerList()
        setFavUser()
    }

    private fun setFavUser() {
        favoriteViewModel = getViewModel(this@FavoriteActivity)
        favoriteViewModel.getAllFavorites().observe(this@FavoriteActivity) { favList ->
            if (favList != null) {
                adapter.setFavorite(favList)
            }
            if (favList.isEmpty()) {
                showNoDataSaved(true)
            } else {
                showNoDataSaved(false)
            }
        }
    }

    private fun showNoDataSaved(bool: Boolean) {
        binding.progessBar.visibility = if (bool) View.VISIBLE else View.GONE
    }

    private fun showRecyclerList() {
        with(binding){
            val layoutManager = LinearLayoutManager(this@FavoriteActivity)
            this.rvFavorite.layoutManager = layoutManager
            this.rvFavorite.adapter = adapter
            adapter.setOnItemCLickCallback(object : FavoriteAdapter.OnItemClickCallback {
                override fun onItemClicked(data: FavoriteEntity) {
                    val intent = Intent(this@FavoriteActivity, DetailUserActivity::class.java)
                    intent.putExtra(DetailUserActivity.KEY_USERNAME, data.login)
                    intent.putExtra(DetailUserActivity.KEY_ID, data.id)
                    startActivity(intent)
                }
            })
        }
    }

    private fun getViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = FavoriteFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }
}
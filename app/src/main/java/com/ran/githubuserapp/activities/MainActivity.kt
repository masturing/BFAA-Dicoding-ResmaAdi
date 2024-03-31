package com.ran.githubuserapp.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ran.githubuserapp.R
import com.ran.githubuserapp.adapter.ListUserAdapter
import com.ran.githubuserapp.databinding.ActivityMainBinding
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.models.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var list: ArrayList<Users> = arrayListOf()
    private val mainViewModel by viewModels<MainViewModel>()
    private val adapter: ListUserAdapter by lazy {
        ListUserAdapter(list)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnFavorite.setOnClickListener {
                startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
            }
            btnSetting.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            }
        }
        searchInit()
        dataInitialization()
        checkProgress()

    }

    private fun dataInitialization() {
        if (checkInternet(this)){
            mainViewModel.user.observe(this) { user ->
                if (user != null) {
                    adapter.addData(user)
                    showRecyclerList()
                }
            }
            mainViewModel.searchUser.observe(this) { searchUser ->
                if (searchUser != null){
                    adapter.addData(searchUser)
                    binding.rvUsers.visibility = View.VISIBLE
                }
            }
        } else {
            Toast.makeText(this,"No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkProgress(){
        mainViewModel.isLoading.observe(this) {
            showProgressBar(it)
        }
    }

    private fun searchInit(){
        with(binding){
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchview.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchview.queryHint = resources.getString(R.string.search_hint)
            searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    showProgressBar(true)
                    p0?.let { mainViewModel.getUserSearch(it) }
                    mainViewModel.searchUser.observe(this@MainActivity) { searchUser ->
                        if (searchUser != null){
                            adapter.addData(searchUser)
                            showRecyclerList()

                        }
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }

            })
            searchview.setOnCloseListener {
                binding.rvUsers.layoutManager = null
                binding.rvUsers.adapter = null
                mainViewModel.user.observe(this@MainActivity) { user ->
                    if (user != null) {
                        adapter.addData(user)
                        showRecyclerList()
                    }
                }
                true
            }
        }
    }

    private fun checkInternet(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network)?: return false
        return when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun showRecyclerList() {
        binding.rvUsers.setHasFixedSize(true)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter

        adapter.setOnItemCLickCallback(object : ListUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Users) {
                val intent = Intent(this@MainActivity, DetailUserActivity::class.java)
                intent.putExtra(DetailUserActivity.EXTRA_USER, data)
                intent.putExtra(DetailUserActivity.KEY_USERNAME, data.login)
                intent.putExtra(DetailUserActivity.KEY_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun showProgressBar(bool: Boolean){
        binding.progessBar.visibility = if (bool) View.VISIBLE else View.GONE
    }


}
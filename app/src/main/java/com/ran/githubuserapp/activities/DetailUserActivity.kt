package com.ran.githubuserapp.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.ran.githubuserapp.R
import com.ran.githubuserapp.adapter.ProfilePagerAdapter
import com.ran.githubuserapp.database.FavoriteEntity
import com.ran.githubuserapp.databinding.ActivityDetailUserBinding
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.models.DetailUserFactory
import com.ran.githubuserapp.models.DetailUserViewModel

@Suppress("DEPRECATION")
class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getStringExtra(KEY_USERNAME)
        user?.let {
            dataInitialization(it)
        }
        with(binding){
            setSupportActionBar(toolbar)
            supportActionBar?.title = "Detail User"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setTitleTextColor(Color.WHITE)
            toolbar.setTitleTextAppearance(this@DetailUserActivity, R.style.HeadingBiggerText)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun dataInitialization(username: String) {
        val user = intent.getParcelableExtra<Users>(EXTRA_USER)
        if (checkInternet(this)) {
            val favorite = FavoriteEntity()
            favorite.login = username
            favorite.id = intent.getIntExtra(KEY_ID, 0)
            favorite.avatar_url = user?.avatarUrl
            val detailUserViewModel: DetailUserViewModel by viewModels {
                DetailUserFactory(username, application)
            }

            detailUserViewModel.isLoading.observe(this@DetailUserActivity) {
                showProgressBar(it)
            }
            detailUserViewModel.isFailed.observe(this@DetailUserActivity) {
                showFailedData(it)
            }
            detailUserViewModel.detailUser.observe(this@DetailUserActivity) { users ->
                if (users != null) {
                    setData(users)
                    setTabLayout(users)
                }
            }
            detailUserViewModel.getFavoriteById(favorite.id!!).observe(this@DetailUserActivity) { favList ->
                isFavorite = favList.isNotEmpty()
                if (favList.isEmpty()) {
                    binding.fabFavorite.imageTintList = ColorStateList.valueOf(Color.rgb(255, 255, 255))
                } else {
                    binding.fabFavorite.imageTintList = ColorStateList.valueOf(Color.rgb(255, 0, 0))
                }
            }
            binding.fabFavorite.apply {
                setOnClickListener {
                    if (isFavorite) {
                        detailUserViewModel.delete(favorite)
                        Toast.makeText(
                            this@DetailUserActivity,
                            "${favorite.login} telah dihapus dari favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        detailUserViewModel.insert(favorite)
                        Toast.makeText(
                            this@DetailUserActivity,
                            "${favorite.login} telah ditambahkan ke favorite",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            binding.btnViewOnGithub.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, "https://github.com/${username}".toUri())
                startActivity(intent)
            }
            binding.btnShare.setOnClickListener {
                val dataUse = intent.getParcelableExtra<Users>(EXTRA_USER) as Users
                val dataSend =
                    "Username: ${dataUse.login.toString()} \n Bio: ${dataUse.bio.toString()} \n Gambar Profile: ${dataUse.avatarUrl.toString()}" +
                            "\n Jumlah Follower: ${dataUse.followers.toString()} \n Jumlah Following: ${dataUse.following.toString()} \n" +
                            "Perusahaan: ${dataUse.company.toString()} \n Alamat: ${dataUse.location.toString()} \n Website: ${dataUse.blog.toString()} \n"
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, dataSend)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        } else {
            showFailedData(true)
            Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTabLayout(users: Users) {
        val profilePagerAdapter = ProfilePagerAdapter(this)
        profilePagerAdapter.model = users
        binding.viewPager.adapter = profilePagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }

    private fun setData(users: Users?) {
        if (users != null) {
            with(binding) {
                tvItemName.text = users.name
                tvItemUname.text = users.login
                tvItemFollow.text =
                    resources.getString(R.string.uFol, users.followers, users.following)
                tvItemCompany.text = users.company ?: "-"
                tvItemLocation.text = users.location ?: "-"
                tvItemWeb.text = users.blog ?: "-"
                tvItemRepo.text = resources.getString(R.string.repo, users.publicRepo)
                Glide.with(root)
                    .load(users.avatarUrl)
                    .apply(RequestOptions().override(500, 500))
                    .into(imgItemPhoto)
            }
        } else {
            Toast.makeText(this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showFailedData(isFailed: Boolean) {
        if (isFailed) {
            Toast.makeText(this, getString(R.string.datafailed_text), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    companion object {
        const val EXTRA_USER = "user"
        const val KEY_USERNAME = "username"
        const val KEY_ID = "extra id"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}
package com.ran.githubuserapp.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.fragment.FollowFragment

class ProfilePagerAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var model: Users? = null
    override fun getItemCount(): Int {
        return 2
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun createFragment(position: Int): Fragment {
        return FollowFragment.newInstance(position + 1, model)
    }
}
package com.ran.githubuserapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ran.githubuserapp.R
import com.ran.githubuserapp.activities.DetailUserActivity
import com.ran.githubuserapp.adapter.ListUserAdapter
import com.ran.githubuserapp.databinding.FragmentFollowBinding
import com.ran.githubuserapp.datasource.Users
import com.ran.githubuserapp.models.FollowFactory
import com.ran.githubuserapp.models.FollowViewModel

class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<Users> = arrayListOf()
    private val adapter: ListUserAdapter by lazy {
        ListUserAdapter(list)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)
        val user = arguments?.getParcelable<Users>(ARG_PARCEL)
        val mIndex = if (index == 1) 1 else 2
        user?.login?.let {
            setViewModel(it, mIndex)
        }
    }

    private fun setViewModel(username: String, index: Int) {
        val followViewModel: FollowViewModel by viewModels {
            FollowFactory(username)
        }
        followViewModel.isLoading.observe(viewLifecycleOwner) {
            showProgressBar(it)
        }
        followViewModel.isFailed.observe(viewLifecycleOwner) {
            showFailed(it)
        }
        if (index == 0){
            followViewModel.followers.observe(viewLifecycleOwner) {
                if (it != null){
                    setData(it)
                }
            }
        } else {
            followViewModel.following.observe(viewLifecycleOwner) {
                if (it != null){
                    setData(it)
                }
            }
        }
    }

    private fun setData(userData: ArrayList<Users>) {
        if(userData.isNotEmpty()) {
            adapter.addData(userData)
            with(binding){
                val layoutManager = LinearLayoutManager(view?.context)
                rvUsers.layoutManager = layoutManager
                rvUsers.adapter = adapter
                adapter.setOnItemCLickCallback(object : ListUserAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Users) {
                        val intent = Intent(context, DetailUserActivity::class.java)
                        intent.putExtra(DetailUserActivity.EXTRA_USER, data)
                        intent.putExtra(DetailUserActivity.KEY_USERNAME, data.login)
                        intent.putExtra(DetailUserActivity.KEY_ID, data.id)
                        startActivity(intent)
                    }
                })
            }
        }
    }

    private fun showProgressBar(bool: Boolean){
        binding.progressBar.visibility = if (bool) View.VISIBLE else View.GONE
    }

    private fun showFailed(bool: Boolean){
        Toast.makeText(context, if (bool) "Data Error" else "Completed", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"
        private const val ARG_PARCEL = "user_model"

        @JvmStatic
        fun newInstance(index: Int, user: Users?) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                    putParcelable(ARG_PARCEL, user)
                }
            }
    }
}
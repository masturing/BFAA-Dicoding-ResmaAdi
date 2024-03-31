package com.ran.githubuserapp.network

import com.ran.githubuserapp.BuildConfig.API_TOKEN
import com.ran.githubuserapp.datasource.Search
import com.ran.githubuserapp.datasource.Users
import de.hdodenhof.circleimageview.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getListUsersAsync(): ArrayList<Users>

    @GET("users/{username}")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getDetailUserAsync(@Path("username") username: String): Users

    @GET("search/users")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    fun getUserBySearch(@Query("q") username: String): Call<Search>

    @GET("users/{username}/followers")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getListFollowers(@Path("username") username: String): ArrayList<Users>

    @GET("users/{username}/following")
    @Headers("Authorization: token $API_TOKEN", "UserResponse-Agent: request")
    suspend fun getListFollowing(@Path("username") username: String): ArrayList<Users>


}
package com.ran.githubuserapp.datasource

import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("total_count")
    val totalCount: Int,

    @SerializedName("incomplete_result")
    val incompleteResult: Boolean,

    val items: ArrayList<Users>?
)

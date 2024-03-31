package com.ran.githubuserapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ran.githubuserapp.R
import com.ran.githubuserapp.database.FavoriteEntity

class FavoriteAdapter (private val userFavorite: ArrayList<FavoriteEntity>) : RecyclerView.Adapter<FavoriteAdapter.FavViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    @SuppressLint("NotifyDataSetChanged")
    fun setFavorite(items: List<FavoriteEntity>) {
        userFavorite.clear()
        userFavorite.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemCLickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_users, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val fav = userFavorite[position]
        Glide.with(holder.itemView.context)
            .load(fav.avatar_url)
            .apply(RequestOptions().override(500, 500))
            .into(holder.imgPhoto)
        holder.tvFname.text = fav.login.toString().replaceFirstChar { it.uppercase() }
        holder.tvDesc.text = buildString {
        append("Github User")
    }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(userFavorite[holder.absoluteAdapterPosition]) }
    }

    override fun getItemCount(): Int {
        return userFavorite.size
    }

    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvFname: TextView = itemView.findViewById(R.id.tv_item_uname)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvDesc : TextView = itemView.findViewById(R.id.tv_item_desc)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: FavoriteEntity)
    }
}

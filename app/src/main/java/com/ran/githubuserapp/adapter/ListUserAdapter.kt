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
import com.ran.githubuserapp.datasource.Users

class ListUserAdapter(private val listUsers: ArrayList<Users>) : RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    @SuppressLint("NotifyDataSetChanged")
    fun addData(items: ArrayList<Users>){
        listUsers.clear()
        listUsers.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemCLickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_users, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val users = listUsers[position]
        Glide.with(holder.itemView.context)
            .load(users.avatarUrl)
            .apply(RequestOptions().override(500,500))
            .into(holder.imgPhoto)
        holder.tvUname.text = users.login.toString().lowercase().replaceFirstChar { it.uppercase() }
        holder.tvDesc.text = buildString {
        append("Github User")
    }
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUsers[holder.absoluteAdapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listUsers.size
    }

    inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvUname: TextView = itemView.findViewById(R.id.tv_item_uname)
        var tvDesc: TextView = itemView.findViewById(R.id.tv_item_desc)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Users)
    }

}
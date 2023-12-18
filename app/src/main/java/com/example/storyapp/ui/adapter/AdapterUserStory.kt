package com.example.storyapp.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ListItemStoryBinding
import com.example.storyapp.ui.activity.detail.DetailActivity

class AdapterUserStory :
    PagingDataAdapter<ListStoryItem, AdapterUserStory.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ListItemStoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    inner class MyViewHolder(private val binding: ListItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val user = getItem(position)
                        val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                            if (user != null) {
                                putExtra(DetailActivity.USER_ID, user.id)
                                putExtra(DetailActivity.USERNAME, user.name)
                                putExtra(DetailActivity.DESCRIPTION, user.description)
                                putExtra(DetailActivity.PICTURE, user.photoUrl)
                            }
                        }
                        itemView.context.startActivity(intent)
                    }
                }
            }

        fun bind(story: ListStoryItem) {
            binding.tvStoryAuthor.text = story.name
            binding.tvStoryDescription.text = story.description
            Glide.with(binding.root.context).load(story.photoUrl).into(binding.ivStoryImage)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

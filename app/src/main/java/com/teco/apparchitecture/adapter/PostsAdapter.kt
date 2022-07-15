package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teco.apparchitecture.databinding.ItemListPostBinding
import com.teco.apparchitecture.model.Post
import java.util.*

class PostsAdapter(private val postList: List<Post>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListPostBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PostViewHolder).bind(postList[position])
    }

    override fun getItemCount() = postList.size

    inner class PostViewHolder(private val binding: ItemListPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.tvPostTitle.text = post.title.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            binding.tvPostDesc.text = post.body.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else it.toString()
            }
        }

    }
}
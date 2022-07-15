package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teco.apparchitecture.R
import com.teco.apparchitecture.databinding.ItemListNewChatUserBinding
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.util.AppUtil

class NewChatUserAdapter(
    private val newChatUserItemClickLister: NewChatUserItemClickLister
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val usersList = ArrayList<FirebaseAppUser>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListNewChatUserBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return NewChatUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NewChatUserViewHolder).bind(usersList[position])
    }

    override fun getItemCount() = usersList.size

    fun addUsersList(users: List<FirebaseAppUser>) {
        this.usersList.clear()
        this.usersList.addAll(users)
        notifyDataSetChanged()
    }

    inner class NewChatUserViewHolder(private val binding: ItemListNewChatUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(firebaseAppUser: FirebaseAppUser) {
            firebaseAppUser.profile?.let { image->
                Glide.with(binding.root).load(AppUtil.decodeImage(image))
                    .placeholder(R.drawable.img_user_placeholder).circleCrop()
                    .into(binding.ivUserProfile)
            }
            binding.tvUserName.text = firebaseAppUser.name
            binding.root.setOnClickListener {
                newChatUserItemClickLister.onUserItemClick(firebaseAppUser)
            }
        }

    }

    interface NewChatUserItemClickLister {
        fun onUserItemClick(firebaseAppUser: FirebaseAppUser)
    }
}
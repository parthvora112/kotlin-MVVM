package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teco.apparchitecture.R
import com.teco.apparchitecture.databinding.ItemListChatListBinding
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.util.AppUtil
import java.text.SimpleDateFormat
import java.util.*


class ChatListAdapter(private val chatListItemClickListener: ChatListItemClickListener) :
    ListAdapter<FirebaseAppUser, RecyclerView.ViewHolder>(ChatListDiffUtil()) {

    val formatter: SimpleDateFormat = SimpleDateFormat("h:mm aa")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListChatListBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatListViewHolder).bind(position)
    }

    inner class ChatListViewHolder(private val binding: ItemListChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val chatListData = getItem(position)
            binding.apply {
                tvUserName.text = "${chatListData.name}"
                tvLastMessage.text = chatListData.lastMessage?.message
                if(chatListData.profile != null) {
                    chatListData.profile?.let { image ->
                        Glide.with(binding.root).load(AppUtil.decodeImage(image))
                            .placeholder(R.drawable.img_user_placeholder).circleCrop()
                            .into(ivUserProfile)

                    }
                }else{
                    ivUserProfile.setImageResource(R.drawable.img_user_placeholder)
                }
                tvMessageTime.text =
                    formatter.format(Date(chatListData.lastMessage?.timeStamp!!.seconds * 1000))
                root.setOnClickListener {
                    chatListItemClickListener.onChatListItemClick(chatListData)
                }
            }
        }

    }

    class ChatListDiffUtil : DiffUtil.ItemCallback<FirebaseAppUser>() {
        override fun areItemsTheSame(oldItem: FirebaseAppUser, newItem: FirebaseAppUser) =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(
            oldItem: FirebaseAppUser,
            newItem: FirebaseAppUser
        ): Boolean {
            return if (oldItem.lastMessage != null && newItem.lastMessage != null) {
                oldItem.lastMessage?.message == newItem.lastMessage?.message
            } else oldItem == newItem
        }

    }

    interface ChatListItemClickListener {
        fun onChatListItemClick(firebaseAppUser: FirebaseAppUser)
    }
}
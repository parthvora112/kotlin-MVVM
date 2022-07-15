package com.teco.apparchitecture.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.teco.apparchitecture.databinding.ListItemChatLeftBinding
import com.teco.apparchitecture.databinding.ListItemChatRightBinding
import com.teco.apparchitecture.model.Message
import java.text.SimpleDateFormat
import java.util.*

class SingleChatMessageAdapter(
    private val currentUserId: String?,
    private val options: FirestoreRecyclerOptions<Message>
) :
    FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType != ChatMessageType.SenderType.messageType) {
            ChatLeftViewHolder(
                ListItemChatLeftBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
            )
        } else {
            ChatRightViewHolder(
                ListItemChatRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        @SuppressLint("SimpleDateFormat")
        val formatter: SimpleDateFormat = SimpleDateFormat("h:mm aa")
        if (model.fromId == currentUserId) {
            (holder as ChatRightViewHolder).bind(model, formatter)
        } else {
            (holder as ChatLeftViewHolder).bind(model, formatter)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).fromId != currentUserId)
            ChatMessageType.ReceiverType.messageType
        else ChatMessageType.SenderType.messageType
    }

    inner class ChatLeftViewHolder(private val binding: ListItemChatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Message, formatter: SimpleDateFormat) {
            binding.tvMessage.text = model.message
            binding.tvTime.text = formatter.format(Date(model.timeStamp!!.seconds * 1000))

            if (model.isShowDate) {
                binding.tvDate.visibility = View.VISIBLE
                val date = model.timeStamp?.toDate()
                when {
                    DateUtils.isToday(date!!.time) -> {
                        binding.tvDate.text = "Today"
                    }
                    DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS) -> {
                        binding.tvDate.text = "Yesterday"
                    }
                    else -> binding.tvDate.text = SimpleDateFormat("MMMM d, yyyy").format(date)
                }
            }
        }

    }

    inner class ChatRightViewHolder(private val binding: ListItemChatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Message, formatter: SimpleDateFormat) {
            if(model.timeStamp == null) model.timeStamp = Timestamp.now()
            binding.tvMessage.text = model.message
            binding.tvTime.text = formatter.format(Date(model.timeStamp!!.seconds * 1000))

            if (model.isShowDate) {
                binding.tvDate.visibility = View.VISIBLE
                val date = model.timeStamp?.toDate()
                when {
                    DateUtils.isToday(date!!.time) -> {
                        binding.tvDate.text = "Today"
                    }
                    DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS) -> {
                        binding.tvDate.text = "Yesterday"
                    }
                    else -> binding.tvDate.text = SimpleDateFormat("MMMM d, yyyy").format(date)
                }
            }
        }

    }

    private enum class ChatMessageType(val messageType: Int) {
        SenderType(1),
        ReceiverType(2)
    }


}
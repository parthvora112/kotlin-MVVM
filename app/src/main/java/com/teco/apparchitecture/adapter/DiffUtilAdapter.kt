package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.teco.apparchitecture.databinding.ItemListDiffUtilBinding
import com.teco.apparchitecture.model.DiffUtilDataModel

class DiffUtilAdapter(private val diffUtilItemClickListener: DiffUtilItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemListDiffUtilBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return DiffUtilViewHolder(binding, diffUtilItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DiffUtilViewHolder).bind(differ.currentList[position])
    }

    private val differCallback = object : DiffUtil.ItemCallback<DiffUtilDataModel>() {
        override fun areItemsTheSame(
            oldItem: DiffUtilDataModel,
            newItem: DiffUtilDataModel
        ) = oldItem.activeStatus == newItem.activeStatus

        override fun areContentsTheSame(
            oldItem: DiffUtilDataModel,
            newItem: DiffUtilDataModel
        ) = oldItem == newItem

    }

    private val differ = AsyncListDiffer(this, differCallback)


    inner class DiffUtilViewHolder(
        private val binding: ItemListDiffUtilBinding,
        private val diffUtilItemClickListener: DiffUtilItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiffUtilDataModel) {
            binding.tvTitle.text = item.title
            binding.tvActiveInactive.text = if (item.activeStatus!!) "active" else "in-active"
            binding.tvActiveInactive.setOnClickListener {
                diffUtilItemClickListener.onDiffUtilItemStatusClick(item)
            }
        }

    }

    interface DiffUtilItemClickListener {
        fun onDiffUtilItemStatusClick(item: DiffUtilDataModel)
    }

    override fun getItemCount() = differ.currentList.size

}
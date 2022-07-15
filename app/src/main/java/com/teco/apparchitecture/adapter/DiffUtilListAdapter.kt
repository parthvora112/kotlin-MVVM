package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teco.apparchitecture.databinding.ItemListDiffUtilBinding
import com.teco.apparchitecture.model.DiffUtilDataModel

class DiffUtilListAdapter(private val diffUtilItemClickListener: DiffUtilAdapter.DiffUtilItemClickListener) :
    ListAdapter<DiffUtilDataModel, RecyclerView.ViewHolder>(DiffUtilListDiffUtil()) {

    inner class DiffUtilListViewHolder(
        private val binding: ItemListDiffUtilBinding,
        private val diffUtilItemClickListener: DiffUtilAdapter.DiffUtilItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiffUtilDataModel) {
            binding.tvTitle.text = item.title
            binding.tvActiveInactive.text = if (item.activeStatus!!) "active" else "in-active"
            binding.tvActiveInactive.setOnClickListener {
                diffUtilItemClickListener.onDiffUtilItemStatusClick(item)
            }
        }
    }

    class DiffUtilListDiffUtil : DiffUtil.ItemCallback<DiffUtilDataModel>() {
        override fun areItemsTheSame(
            oldItem: DiffUtilDataModel,
            newItem: DiffUtilDataModel
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DiffUtilDataModel,
            newItem: DiffUtilDataModel
        ) = oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemListDiffUtilBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return DiffUtilListViewHolder(binding, diffUtilItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DiffUtilListViewHolder).bind(getItem(position))
    }
}
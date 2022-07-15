package com.teco.apparchitecture.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teco.apparchitecture.databinding.ItemListOptionsBinding
import com.teco.apparchitecture.model.OptionModel
import com.teco.apparchitecture.util.AppUtil

class OptionsAdapter(
    private val optionsList: List<OptionModel>,
    private val onOptionItemClickListener: OnOptionItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListOptionsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OptionsViewHolder(binding, onOptionItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OptionsViewHolder).bind(optionsList[position])
    }

    override fun getItemCount() = optionsList.size

    inner class OptionsViewHolder(
        private val binding: ItemListOptionsBinding,
        private val onOptionItemClickListener: OnOptionItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(optionModel: OptionModel) {
            binding.ivOptionBanner.setImageResource(optionModel.resId!!)
            binding.tvOptionTitle.text = optionModel.title
            binding.root.setOnClickListener {
                onOptionItemClickListener.onOptionItemClick(optionModel.type!!)
            }
        }
    }

    interface OnOptionItemClickListener {
        fun onOptionItemClick(optionType: AppUtil.OptionType)
    }
}
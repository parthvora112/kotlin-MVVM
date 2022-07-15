package com.teco.apparchitecture.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teco.apparchitecture.databinding.ItemListTodoBinding
import com.teco.apparchitecture.model.ToDo

class TodoAdapter(private val onToDoItemClickListener: OnToDoItemClickListener) : ListAdapter<ToDo, RecyclerView.ViewHolder>(ToDoDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemListTodoBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return ToDoViewHolder(
            binding,
            onToDoItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ToDoViewHolder).bind(getItem(position))
    }

    inner class ToDoViewHolder(
        private val binding: ItemListTodoBinding,
        private val onToDoItemClickListener: OnToDoItemClickListener
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(todo: ToDo){
            Log.e("ToDoAdapter", "bind: $todo :: ${todo.isExpand}", )
            binding.apply {
                tvTaskTitle.text = todo.title
                ivExpandCollapse.isVisible = !todo.description?.isEmpty()!!
                ivExpandCollapse.setOnClickListener {
                    onToDoItemClickListener.onToDoItemExpandCollapseClick(todo)
                }
                tvTaskDesc.text = todo.description
                tvTaskDesc.isVisible = todo.isExpand!!
                if(todo.isExpand!!){
                    tvTaskDesc.visibility = View.VISIBLE
                }else{
                    tvTaskDesc.visibility = View.GONE
                }
            }
        }
    }

    class ToDoDiffCallback: DiffUtil.ItemCallback<ToDo>(){
        override fun areItemsTheSame(oldItem: ToDo, newItem: ToDo) : Boolean{
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ToDo, newItem: ToDo): Boolean{
            Log.e("Adapter", "areContentsTheSame: $oldItem :: $newItem  =====> ${oldItem == newItem}", )
            if(oldItem.isExpand == newItem.isExpand) return false
            return oldItem == newItem
        }

    }

    interface OnToDoItemClickListener{
        fun onToDoItemExpandCollapseClick(todo: ToDo)
    }
}
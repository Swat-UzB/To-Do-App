package com.example.todoapplication.fragments.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.data.models.ToDoData
import com.example.todoapplication.databinding.RowLayoutBinding
import com.example.todoapplication.fragments.list.SwipeToDelete

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    var dataList = emptyList<ToDoData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = RowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(dataList[position])

    }


    fun setData(toDoData: List<ToDoData>) {
        val toDoDiffUtil = ToDoDiffUtil(dataList, toDoData)
        val toDoDIffResult = DiffUtil.calculateDiff(toDoDiffUtil)
        this.dataList = toDoData
        toDoDIffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = dataList.size

    class ListViewHolder(val binding: RowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(toDoData: ToDoData) {
            binding.toDoData = toDoData
            binding.executePendingBindings()
        }
    }
}
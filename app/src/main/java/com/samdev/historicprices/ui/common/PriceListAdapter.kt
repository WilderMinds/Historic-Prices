package com.samdev.historicprices.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.databinding.ListItemPriceHistoryBinding

class PriceListAdapter : ListAdapter<Price, PriceListAdapter.ItemViewHolder>(PriceDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding = ListItemPriceHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ItemViewHolder(
        private val binding: ListItemPriceHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(price: Price) = with(itemView) {
            // Set data to your item view here
            binding.item = price

            setOnClickListener {
                onClick(price)
            }
        }

        private fun onClick(item: Price) {
            // Handle item click actions
        }
    }
}

class PriceDiffCallback : DiffUtil.ItemCallback<Price>() {
    override fun areItemsTheSame(
        oldItem: Price,
        newItem: Price
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Price,
        newItem: Price
    ): Boolean = oldItem == newItem
}


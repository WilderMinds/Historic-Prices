package com.samdev.historicprices.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.databinding.ListItemProductBinding
import com.samdev.historicprices.utils.OnClickItem

class ProductListAdapter(private val clickListener: OnClickItem) :
    ListAdapter<ProductWithPrices, ProductListAdapter.ItemViewHolder>(ProductWithPricesDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val binding = ListItemProductBinding.inflate(
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
        val binding: ListItemProductBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductWithPrices) = with(itemView) {
            // Set data to your item view here
            binding.item = product

            setOnClickListener {
                onClick(product)
            }
        }

        private fun onClick(item: ProductWithPrices) {
            clickListener.onClick(item)
        }
    }
}

class ProductWithPricesDiffCallback : DiffUtil.ItemCallback<ProductWithPrices>() {
    override fun areItemsTheSame(
        oldItem: ProductWithPrices,
        newItem: ProductWithPrices
    ): Boolean {
        return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(
        oldItem: ProductWithPrices,
        newItem: ProductWithPrices
    ): Boolean {
        return oldItem == newItem
    }
}
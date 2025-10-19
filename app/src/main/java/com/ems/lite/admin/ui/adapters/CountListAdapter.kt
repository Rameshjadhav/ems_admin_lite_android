package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemCountBinding
import com.ems.lite.admin.model.table.CountBy
import com.ems.lite.admin.utils.BindingViewHolder

internal class CountListAdapter(
    items: ArrayList<CountBy>
) : RecyclerView.Adapter<BindingViewHolder<ItemCountBinding>>() {
    private var items: ArrayList<CountBy> = arrayListOf()
    private lateinit var context: Context
    var countClickListener: CountClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemCountBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_count, parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemCountBinding>, position: Int) {
        val count = items[position]
        holder.binding.count = count
        holder.binding.countClickListener = countClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface CountClickListener {
        fun onItemClick(count: CountBy)
    }
}
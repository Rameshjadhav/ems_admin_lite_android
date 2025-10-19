package com.ems.lite.admin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemRelativeCountBinding
import com.ems.lite.admin.model.RelativeCount
import com.ems.lite.admin.utils.BindingViewHolder

internal class RelativeCountListAdapter(items: ArrayList<RelativeCount>) :
    RecyclerView.Adapter<BindingViewHolder<ItemRelativeCountBinding>>() {

    private var items: ArrayList<RelativeCount> = arrayListOf()
    private lateinit var context: Context
    var relativeListener: RelativeListener? = null
    private var isBinding = false

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemRelativeCountBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_relative_count, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemRelativeCountBinding>, position: Int
    ) {
        val relativeCount = items[position]
        isBinding = true
        holder.binding.relativeCount = relativeCount
        holder.binding.relativeCountListener = relativeListener
        holder.binding.executePendingBindings()
        isBinding = false
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface RelativeListener {
        fun onItemSelect(relative: RelativeCount)
    }
}
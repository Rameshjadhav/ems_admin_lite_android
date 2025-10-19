package com.ems.lite.admin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemRelativeBinding
import com.ems.lite.admin.model.Relative
import com.ems.lite.admin.utils.BindingViewHolder

internal class RelativeListAdapter(items: ArrayList<Relative>) :
    RecyclerView.Adapter<BindingViewHolder<ItemRelativeBinding>>() {

    private var items: ArrayList<Relative> = arrayListOf()
    private lateinit var context: Context
    var relativeListener: RelativeListener? = null
    private var isBinding = false

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemRelativeBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_relative, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemRelativeBinding>, position: Int
    ) {
        val relative = items[position]
        isBinding = true
        holder.binding.relative = relative
        holder.binding.relativeListener = relativeListener
        holder.binding.executePendingBindings()
        isBinding = false
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface RelativeListener {
        fun onItemSelect(relative: Relative)
        fun onCallClick(mobile: String?)
    }
}
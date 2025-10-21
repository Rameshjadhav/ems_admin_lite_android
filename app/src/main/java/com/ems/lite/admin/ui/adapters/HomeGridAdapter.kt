package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.model.HomeOption
import com.ems.lite.admin.utils.BindingViewHolder
import com.ems.lite.admin.databinding.ItemHomeGridBinding

internal class HomeGridAdapter(items: ArrayList<HomeOption>) :
    RecyclerView.Adapter<BindingViewHolder<ItemHomeGridBinding>>() {
    private var items: ArrayList<HomeOption> = arrayListOf()
    private lateinit var context: Context
    var homeOptionClickListener: HomeOptionClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemHomeGridBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_grid, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemHomeGridBinding>, position: Int) {
        val homeOption = items[position]
        holder.binding.homeOption = homeOption
        holder.binding.homeOptionClickListener = homeOptionClickListener
        holder.binding.ivHomeOption.setImageResource(homeOption.icon)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface HomeOptionClickListener {
        fun onItemClick(homeOption: HomeOption)
    }
}
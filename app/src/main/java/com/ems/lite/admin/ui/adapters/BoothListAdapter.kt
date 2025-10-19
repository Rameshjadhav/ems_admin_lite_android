package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemBoothBinding
import com.ems.lite.admin.model.table.Booth
import com.ems.lite.admin.utils.BindingViewHolder

internal class BoothListAdapter(
    items: ArrayList<Booth>
) : RecyclerView.Adapter<BindingViewHolder<ItemBoothBinding>>() {
    private var items: ArrayList<Booth> = arrayListOf()
    private lateinit var context: Context
    var selectedBooth: Booth? = null
    var boothClickListener: BoothClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemBoothBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_booth,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemBoothBinding>, position: Int) {
        val booth = items[position]
        holder.binding.booth = booth
        holder.binding.ivSelected.visibility =
            if (selectedBooth != null && selectedBooth?.boothNo == booth.boothNo) View.VISIBLE else View.GONE
        holder.binding.boothClickListener = boothClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface BoothClickListener {
        fun onItemClick(booth: Booth)
    }
}
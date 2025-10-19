package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemVillageBinding
import com.ems.lite.admin.model.table.Village
import com.ems.lite.admin.utils.BindingViewHolder

internal class VillageListAdapter(
    items: ArrayList<Village>
) : RecyclerView.Adapter<BindingViewHolder<ItemVillageBinding>>() {
    private var items: ArrayList<Village> = arrayListOf()
    private lateinit var context: Context
    var selectedVillage: Village? = null
    var villageClickListener: VillageClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemVillageBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_village,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemVillageBinding>, position: Int) {
        val village = items[position]
        holder.binding.village = village
        holder.binding.ivSelected.visibility =
            if (selectedVillage != null && selectedVillage?.villageNo == village.villageNo) View.VISIBLE else View.GONE
        holder.binding.villageClickListener = villageClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface VillageClickListener {
        fun onItemClick(village: Village)
    }
}
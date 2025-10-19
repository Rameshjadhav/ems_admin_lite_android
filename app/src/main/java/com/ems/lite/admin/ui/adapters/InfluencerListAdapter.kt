package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemInfluencerBinding
import com.ems.lite.admin.model.Influencer
import com.ems.lite.admin.utils.BindingViewHolder

internal class InfluencerListAdapter(
    items: ArrayList<Influencer>
) : RecyclerView.Adapter<BindingViewHolder<ItemInfluencerBinding>>() {
    private var items: ArrayList<Influencer> = arrayListOf()
    private lateinit var context: Context
    var voterClickListener: VoterClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemInfluencerBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_influencer,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemInfluencerBinding>, position: Int) {
        val influencer = items[position]
        holder.binding.influencer = influencer
        holder.binding.voterClickListener = voterClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface VoterClickListener {
        fun onItemClick(influencer: Influencer)
        fun onCallClick(influencer: Influencer)
        fun onWhatsAppClick(influencer: Influencer)
    }
}
package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemVoterBinding
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.BindingViewHolder

internal class VoterListAdapter(
    items: ArrayList<Voter>
) : RecyclerView.Adapter<BindingViewHolder<ItemVoterBinding>>() {
    private var items: ArrayList<Voter> = arrayListOf()
    private lateinit var context: Context
    var selectedVoter: Voter? = null
    var voterClickListener: VoterClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemVoterBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_voter,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemVoterBinding>, position: Int) {
        val voter = items[position]
        holder.binding.voter = voter
        holder.binding.voterClickListener = voterClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface VoterClickListener {
        fun onItemClick(voter: Voter)
        fun onCallClick(mobileNo: String?)
    }
}
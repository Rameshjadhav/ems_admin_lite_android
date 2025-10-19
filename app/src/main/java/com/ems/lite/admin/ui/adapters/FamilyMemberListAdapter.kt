package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemFamilyMemberBinding
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.BindingViewHolder


internal class FamilyMemberListAdapter(
    items: ArrayList<Voter>, private val isFromReportDay: Boolean = false
) : RecyclerView.Adapter<BindingViewHolder<ItemFamilyMemberBinding>>() {
    private var items: ArrayList<Voter> = arrayListOf()
    private lateinit var context: Context
    var voterClickListener: VoterClickListener? = null
    private var isBinding = false

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemFamilyMemberBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_family_member, parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: BindingViewHolder<ItemFamilyMemberBinding>, position: Int
    ) {
        isBinding = true
        val voter = items[position]
        holder.binding.voter = voter
        holder.binding.voterClickListener = voterClickListener
        holder.binding.tvMobileNumber.visibility = if (isFromReportDay) View.VISIBLE else View.GONE
        holder.binding.headSwitch.isChecked = (voter.familyHead == 1)
        holder.binding.tvRelative.visibility =
            if (voter.familyHead == 1) View.VISIBLE else View.GONE
        holder.binding.headSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (!isBinding) {
                voter.familyHead = if (isChecked) 1 else 0
                voter.updated = 1
                holder.binding.tvRelative.visibility =
                    if (voter.familyHead == 1) View.VISIBLE else View.GONE
                voterClickListener?.onHeadChanged(voter)
            }
        })
        holder.binding.executePendingBindings()
        isBinding = false
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface VoterClickListener {
        fun onItemClick(voter: Voter)
        fun onCallClick(mobileNo: String?)
        fun removeFamilyMembe(voter: Voter)
        fun onHeadChanged(voter: Voter)
        fun onRelativeClick(voter: Voter)
    }
}
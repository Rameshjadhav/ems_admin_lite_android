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
import com.ems.lite.admin.model.dto.FamilyVoterDto
import com.ems.lite.admin.model.table.Voter
import com.ems.lite.admin.utils.BindingViewHolder


internal class FamilyMemberListAdapter(
    items: ArrayList<FamilyVoterDto>, private val isFromReportDay: Boolean = false
) : RecyclerView.Adapter<BindingViewHolder<ItemFamilyMemberBinding>>() {
    private var items: ArrayList<FamilyVoterDto> = arrayListOf()
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
        val familyVoterDto = items[position]
        holder.binding.familyVoterDto = familyVoterDto
        holder.binding.voterClickListener = voterClickListener
        holder.binding.tvMobileNumber.visibility = if (isFromReportDay) View.VISIBLE else View.GONE
        holder.binding.headSwitch.isChecked = (familyVoterDto.voter.familyHead == 1)
        holder.binding.tvRelative.visibility =
            if (familyVoterDto.voter.familyHead == 1) View.VISIBLE else View.GONE
        holder.binding.headSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (!isBinding) {
                familyVoterDto.voter.familyHead = if (isChecked) 1 else 0
                familyVoterDto.voter.updated = 1
                holder.binding.tvRelative.visibility =
                    if (familyVoterDto.voter.familyHead == 1) View.VISIBLE else View.GONE
                voterClickListener?.onHeadChanged(familyVoterDto)
            }
        })
        holder.binding.executePendingBindings()
        isBinding = false
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface VoterClickListener {
        fun onItemClick(familyVoterDto: FamilyVoterDto)
        fun onCallClick(mobileNo: String?)
        fun removeFamilyMembe(familyVoterDto: FamilyVoterDto)
        fun onHeadChanged(familyVoterDto: FamilyVoterDto)
        fun onRelativeClick(familyVoterDto: FamilyVoterDto)
    }
}
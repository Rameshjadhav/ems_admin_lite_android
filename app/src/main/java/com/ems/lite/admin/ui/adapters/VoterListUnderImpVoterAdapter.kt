package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.model.table.Voter
import dagger.hilt.android.scopes.ActivityScoped
import kotlin.collections.ArrayList

@ActivityScoped
class VoterListUnderImpVoterAdapter(
    private val items: ArrayList<Voter>,
    private val listener: ItemListener
) :

    RecyclerView.Adapter<VoterViewHolder1>() {
    private var lastPosition = -1

    private lateinit var context: Context

    interface ItemListener {
        fun onVoterClick(voter: Voter)
        fun onCallClick(voter: Voter)
        fun onDeleteClick(voter: Voter)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoterViewHolder1 {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_voter_under_imp_voter, parent, false)

        context = parent.getContext();
        return VoterViewHolder1(view)
    }

    override fun getItemCount(): Int = items.size


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: VoterViewHolder1, position: Int) {


        Log.d("Sunil", items[position].toString())
        holder.rootLayout.tag = items[position]
        val item = items[position]
        holder.wardNameText.text = item.villageNo.toString()
        holder.nameText.text = if (!item.getFullName().isNullOrEmpty()) item.getFullName() else ""
        holder.mobileNumberText.text = if (!item.mobileNo.isNullOrEmpty()) item.mobileNo else "-"
        holder.mobileNumberText.visibility =
            if (!item.mobileNo.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
        holder.rootLayout.setOnClickListener {
            listener.onVoterClick(items[position])
        }
        holder.mobileNumberText.setOnClickListener {
            listener.onCallClick(items[position])
        }
        holder.ivDelete.setOnClickListener {
            listener.onDeleteClick(items[position])
        }
    }
}

class VoterViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)

    val wardNameText: TextView = itemView.findViewById(R.id.wardNameText)
    val nameText: TextView = itemView.findViewById(R.id.nameText)
    val mobileNumberText: TextView = itemView.findViewById(R.id.mobileNumberText)
    val ivDelete: ImageView = itemView.findViewById(R.id.iv_delete)
}
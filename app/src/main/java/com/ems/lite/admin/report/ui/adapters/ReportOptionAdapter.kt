package com.ems.lite.admin.report.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemReportOptionBinding
import com.ems.lite.admin.report.model.ReportOption
import com.ems.lite.admin.utils.BindingViewHolder

internal class ReportOptionAdapter(items: ArrayList<ReportOption>) :
    RecyclerView.Adapter<BindingViewHolder<ItemReportOptionBinding>>() {
    private var items: ArrayList<ReportOption> = arrayListOf()
    private lateinit var context: Context
    var reportOptionClickListener: ReportOptionClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemReportOptionBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_report_option, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemReportOptionBinding>, position: Int) {
        val reportOption = items[position]
        holder.binding.reportOption = reportOption
        holder.binding.reportOptionClickListener = reportOptionClickListener
        holder.binding.ivHomeOption.setImageResource(reportOption.icon)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface ReportOptionClickListener {
        fun onItemClick(reportOption: ReportOption)
    }
}
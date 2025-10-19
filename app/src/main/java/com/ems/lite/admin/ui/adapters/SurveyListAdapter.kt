package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.model.Survey
import com.ems.lite.admin.utils.CommonUtils
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class SurveyListAdapter(private val items: ArrayList<Survey>, private val listener: ItemListener) :

    RecyclerView.Adapter<SurwayViewHolder>() {
    private var lastPosition = -1

    private lateinit var context: Context

    interface ItemListener {
        fun onSurwayClick(survey: Survey)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurwayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_survey, parent, false)

        context = parent.getContext();
        return SurwayViewHolder(view)
    }

    override fun getItemCount(): Int = items.size


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: SurwayViewHolder, position: Int) {


        holder.rootLayout.tag = items[position]
        val item = items[position]
        holder.tvBoothName.text = item.getBoothName()
        holder.tvTotalVoter.text = "" + item.totalcount
        val mobilePercent =
            CommonUtils.roundDouble(((item.mobileCount.toDouble() / item.totalcount) * 100), 2)

        holder.tvMobleTotalCount.text = "" + item.mobileCount + " ($mobilePercent%)"
        val percent =
            CommonUtils.roundDouble(((item.casteCount.toDouble() / item.totalcount) * 100), 2)
        holder.tvTotalCasteCount.text = "" + item.casteCount + " ($percent%)"
        holder.rootLayout.setOnClickListener {
            listener.onSurwayClick(items[position])
        }
    }

}

class SurwayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val rootLayout: ConstraintLayout = itemView.findViewById(R.id.rootLayout)

    val tvBoothName: TextView = itemView.findViewById(R.id.tv_booth_name)
    val tvTotalVoter: TextView = itemView.findViewById(R.id.tv_total_voter)
    val tvMobleTotalCount: TextView = itemView.findViewById(R.id.tv_total_mobile_num_count)
    val tvTotalCasteCount: TextView = itemView.findViewById(R.id.tv_total_caste_count)
}
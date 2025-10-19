package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ems.lite.admin.R
import com.ems.lite.admin.model.Taluka
import com.ems.lite.admin.model.table.Cast
import com.ems.lite.admin.model.table.Designation
import com.ems.lite.admin.model.table.Profession

class SpinnerAdapter(
    context: Context?, resourceId: Int, val list: List<Any>?,
    val disabledPosition: Int = 0
) : ArrayAdapter<Any?>(context!!, resourceId, list!!) {
    private var inflater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return rowView(convertView, position)
    }

    @SuppressLint("InflateParams")
    private fun rowView(convertView: View?, position: Int): View {
        val holder: ViewHolder
        var rowView = convertView
        if (rowView == null) {
            holder = ViewHolder()
            inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = inflater!!.inflate(
                R.layout.spinner_item,
                null,
                false
            )
            holder.txtTitle =
                rowView.findViewById<View>(R.id.text1) as TextView
            rowView.tag = holder
        } else {
            holder = rowView.tag as ViewHolder
        }
        when (list!![position]) {
            is Designation -> {
                holder.txtTitle!!.text = (list[position] as Designation).toString()
            }
            is Cast -> {
                holder.txtTitle!!.text = (list[position] as Cast).toString()
            }
            is Taluka -> {
                holder.txtTitle!!.text = (list[position] as Taluka).talukaName
            }
            is Profession -> {
                holder.txtTitle!!.text = (list[position] as Profession).toString()
            }
            is String -> {
                holder.txtTitle!!.text = (list[position] as String)
            }
        }
        return rowView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val tv = view as TextView
        if (position == disabledPosition) {
            // Set the hint text color gray
            tv.setTextColor(ContextCompat.getColor(context, R.color.et_hint_color))
        } else {
            tv.setTextColor(ContextCompat.getColor(context, R.color.primary_text_color))
        }
        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    private inner class ViewHolder {
        var txtTitle: TextView? = null
    }
}
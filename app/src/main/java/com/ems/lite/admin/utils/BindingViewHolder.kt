package com.ems.lite.admin.utils

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BindingViewHolder<T>(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
    val binding: T = DataBindingUtil.bind<ViewDataBinding>(itemView!!) as T

}
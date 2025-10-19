package com.ems.lite.admin.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ems.lite.admin.R
import com.ems.lite.admin.databinding.ItemUserBinding
import com.ems.lite.admin.model.User
import com.ems.lite.admin.utils.BindingViewHolder

internal class UserListAdapter(
    items: ArrayList<User>
) : RecyclerView.Adapter<BindingViewHolder<ItemUserBinding>>() {
    private var items: ArrayList<User> = arrayListOf()
    private lateinit var context: Context
    var userClickListener: UserClickListener? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ItemUserBinding> {
        context = parent.context
        return BindingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_user,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder<ItemUserBinding>, position: Int) {
        val user = items[position]
        holder.binding.user = user
        if (user.isActive == 1) {
            holder.binding.statusText.text = context.getString(R.string.active)
            holder.binding.statusText.setTextColor(
                ContextCompat.getColor(context, R.color.theme_color)
            )
            holder.binding.cvStatus.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.active_bg_color)
            )
        } else {
            holder.binding.statusText.text = context.getString(R.string.inactive)
            holder.binding.statusText.setTextColor(
                ContextCompat.getColor(context, R.color.red_text_color)
            )
            holder.binding.cvStatus.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.inactive_bg_color)
            )
        }
        holder.binding.userClickListener = userClickListener
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface UserClickListener {
        fun onItemClick(user: User)
    }
}
package com.mr.ahmedlearninapp.ui.active_users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mr.ahmedlearninapp.*
import java.util.*

class ActivateUsersAdapter(
    private val users: List<User>,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<ActivateUsersAdapter.ViewHolder>() {

    // holder class to hold reference
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //get view reference
        var name = view.findViewById(R.id.name) as TextView
        var phone = view.findViewById(R.id.phone) as TextView
        var checkbox = view.findViewById(R.id.checkbox) as CheckBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create view holder to hold reference
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_student_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set values
        holder.name.text = users[position].name
        holder.phone.text = users[position].phone
        var isDefultValue = true
        val calender = Calendar.getInstance()
        val oldCalender = Calendar.getInstance()
        oldCalender.set(users[position].year, users[position].month, users[position].day)
        holder.checkbox.isChecked = !calender.after(oldCalender)
        holder.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && !isDefultValue) {
                onClickListener.onClick(users[position])
            }
            isDefultValue = false
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    interface OnClickListener {
        fun onClick(user: User)
    }
}
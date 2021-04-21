package com.mr.ahmedlearninapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LessonsAdapter(private val lessons: List<Lesson>)
    : RecyclerView.Adapter<LessonsAdapter.ViewHolder>() {

    // holder class to hold reference
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //get view reference
        var name  = view.findViewById(R.id.name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create view holder to hold reference
        return ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.lessons_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set values
        holder.name.text =  lessons[position].name

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, LessonDetailsActivity::class.java)
            intent.putExtra(LESSON_NAME,lessons[position].name)
            intent.putExtra(LESSON_ID,lessons[position].id)
            intent.putExtra(URL_VIDEO,lessons[position].url)
            intent.putExtra(URL_PDF,lessons[position].pdf)
            intent.putExtra(URL_EXAM,lessons[position].examUrl)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lessons.size
    }

}
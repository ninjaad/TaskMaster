package com.example.todoapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list: List<TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_todo, parent, false)
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todoModel: TodoModel) {
            with(itemView) {
                // Set random color for tag
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors[Random().nextInt(colors.size)]
                val viewColorTag = findViewById<View>(R.id.viewColorTag)
                viewColorTag.setBackgroundColor(randomColor)

                // Set basic task information
                val txtShowTitle = findViewById<TextView>(R.id.txtShowTitle)
                val txtShowTask = findViewById<TextView>(R.id.txtShowTask)
                val txtShowCategory = findViewById<TextView>(R.id.txtShowCategory)


                txtShowTitle.text = todoModel.title
                txtShowTask.text = todoModel.description
                txtShowCategory.text = todoModel.category

                // Update date and time with new formats
                updateDate(todoModel.date)
                updateTime(todoModel.date) // Using the same timestamp for consistency

                // Handle edit button click

            }
        }

        private fun updateTime(timestamp: Long) {
            try {
                val myFormat = "HH:mm:ss" // 24-hour format with seconds
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                val txtShowTime = itemView.findViewById<TextView>(R.id.txtShowTime)
                txtShowTime.text = sdf.format(Date(timestamp))
            } catch (e: Exception) {
                // Handle invalid timestamp
                val txtShowTime = itemView.findViewById<TextView>(R.id.txtShowTime)
                txtShowTime.text = "--:--:--"
            }
        }

        private fun updateDate(timestamp: Long) {
            try {
                val myFormat = "yyyy-MM-dd" // ISO format
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                val txtShowDate = itemView.findViewById<TextView>(R.id.txtShowDate)
                txtShowDate.text = sdf.format(Date(timestamp))
            } catch (e: Exception) {
                // Handle invalid timestamp
                val txtShowDate = itemView.findViewById<TextView>(R.id.txtShowDate)
                txtShowDate.text = "----/--/--"
            }
        }
    }
}
package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import kotlin.random.Random
import java.util.*

class TodoAdapter (val list : List<TodoModel>):RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
       return TodoViewHolder(
           LayoutInflater.from(parent.context)
               .inflate(R.layout.item_todo,parent,false)
       )
    }

    override fun getItemCount()=list.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }
    class TodoViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {

        fun bind(todoModel: TodoModel)
        {

            with(itemView)
            {
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors[java.util.Random().nextInt(colors.size)]
                val viewColorTag=findViewById<View>(R.id.viewColorTag)
                viewColorTag.setBackgroundColor(randomColor)
                val txtShowTitle=findViewById<TextView>(R.id.txtShowTitle)
                val txtShowTask=findViewById<TextView>(R.id.txtShowTask)
                val txtShowCategory=findViewById<TextView>(R.id.txtShowCategory)
                txtShowTitle.text=todoModel.title
                txtShowTask.text=todoModel.description
                txtShowCategory.text=todoModel.category
                updateTime(todoModel.time)
                updateDate(todoModel.date)


            }

        }

        private fun updateTime(time:Long) {
            //Mon, 5 Jan 2020
            val myFormat= "h:mm a"
            val sdf= SimpleDateFormat(myFormat)
            val txtShowTime=itemView.findViewById<TextView>(R.id.txtShowTime)
            txtShowTime.text=(sdf.format(Date(time)))

        }

        private fun updateDate(time:Long) {
            //Mon, 5 Jan 2020
            val myFormat= "EEE, d MMM yyyy"
            val sdf=SimpleDateFormat(myFormat)
            val txtShowDate=itemView.findViewById<TextView>(R.id.txtShowDate)
            txtShowDate.text=(sdf.format(Date(time)))

        }

    }


}
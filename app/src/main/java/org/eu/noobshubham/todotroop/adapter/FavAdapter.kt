package org.eu.noobshubham.todotroop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.eu.noobshubham.ToDoTroop.R
import org.eu.noobshubham.todotroop.model.Todo


class FavAdapter(private val todoList: ArrayList<Todo>) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo: Todo = todoList[position]
        holder.bindItems(todo)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(todo: Todo) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
            tvTitle.text = todo.title
            tvDesc.text = todo.desc
        }
    }
}
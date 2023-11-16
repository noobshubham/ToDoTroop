package org.eu.noobshubham.todotroop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.eu.noobshubham.ToDoTroop.R
import org.eu.noobshubham.todotroop.model.Todo


class MyAdapter(private val todoList: ArrayList<Todo>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private var listener: OnClickListener? = null

    fun setListener(clickListener: OnClickListener) {
        this.listener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo: Todo = todoList[position]
        holder.bindItems(todo)

        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener!!.onItemClick(todo, position)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (listener != null) {
                listener!!.onItemLongClick(todo)
            }
            true
        }

        holder.itemView.findViewById<ImageView>(R.id.btDelete).setOnClickListener {
            if (listener != null) {
                listener!!.onItemDelete(todo)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(todo: Todo) {
            val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
            val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
            tvTitle.text = todo.title
            tvDesc.text = todo.desc
        }
    }

    interface OnClickListener {
        fun onItemClick(todo: Todo, position: Int)
        fun onItemDelete(todo: Todo)
        fun onItemLongClick(todo: Todo)
    }
}
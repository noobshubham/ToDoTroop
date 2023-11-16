package org.eu.noobshubham.todotroop

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.eu.noobshubham.ToDoTroop.R
import org.eu.noobshubham.todotroop.adapter.FavAdapter
import org.eu.noobshubham.todotroop.helper.DBHelper
import org.eu.noobshubham.todotroop.model.Todo


class FavouriteActivity : AppCompatActivity() {

    private var myAdapter: FavAdapter? = null
    private var dbHelper: DBHelper? = null
    private var todoList = ArrayList<Todo>()
    private lateinit var recyclerView: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Navigate to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_favorite -> {
                    // Stay on FavActivity (current activity)
                    true
                }
                else -> false
            }
        }

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerView)

        //Set the TodoList in myAdapter
        getFavTodoList()
    }

    private fun getFavTodoList() {
        todoList = dbHelper!!.getAllFavourites
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myAdapter = FavAdapter(todoList)
        recyclerView.adapter = myAdapter
    }
}
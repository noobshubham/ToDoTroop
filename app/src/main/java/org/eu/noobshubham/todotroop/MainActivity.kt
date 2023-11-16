package org.eu.noobshubham.todotroop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.eu.noobshubham.ToDoTroop.R
import org.eu.noobshubham.todotroop.adapter.MyAdapter
import org.eu.noobshubham.todotroop.api.RetrofitBuilder
import org.eu.noobshubham.todotroop.helper.DBHelper
import org.eu.noobshubham.todotroop.model.Todo
import org.eu.noobshubham.todotroop.model.WeatherData
import retrofit2.Response

class MainActivity : AppCompatActivity(), MyAdapter.OnClickListener {
    override fun onItemDelete(todo: Todo) {
        deleteConfirmation(todo)
    }

    override fun onItemLongClick(todo: Todo) {
        addToFavorites(todo)
    }

    override fun onItemClick(todo: Todo, position: Int) {
        showNoteDialog(true, todo, position)
    }

    private var myAdapter: MyAdapter? = null
    private var dbHelper: DBHelper? = null
    private var todoList = ArrayList<Todo>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var response: Response<WeatherData>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        dbHelper = DBHelper(this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Navigate to MainActivity (current activity)
                    true
                }
                R.id.menu_favorite -> {
                    // Navigate to FavActivity
                    val intent = Intent(this, FavouriteActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerView)

        fab.setOnClickListener {
            showNoteDialog(false, null, -1)
        }
        //Set the TodoList in myAdapter
        getTodoList()

        GlobalScope.launch {
            val deferredResponse = async {
                RetrofitBuilder().networkInterface.getCurrentWeather()
            }
            response = deferredResponse.await()

            // Update the UI on the main thread
            runOnUiThread {
                val currentTemp = findViewById<TextView>(R.id.currentTemperatureTextView)
                val location = findViewById<TextView>(R.id.locationTextView)
                currentTemp.text = "${response.body()?.current?.temp_c}°C"
                location.text =
                    "${response.body()?.location?.name}, ${response.body()?.location?.region}"
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if (myAdapter != null) {
            getTodoList()
            myAdapter!!.notifyDataSetChanged()
        }
    }

    private fun getTodoList() {
        todoList = dbHelper!!.allNotes
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myAdapter = MyAdapter(todoList)
        myAdapter!!.setListener(this)
        recyclerView.adapter = myAdapter
    }

    private fun deleteConfirmation(todo: Todo) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Confirm Delete...")
        alertDialog.setMessage("Are you sure you want to delete this?")
        alertDialog.setIcon(R.drawable.baseline_delete_24)
        alertDialog.setPositiveButton("YES") { _, _ ->
            dbHelper!!.deleteTodo(todo)
            getTodoList()  // refreshing the list
        }

        alertDialog.setNegativeButton("NO") { dialog, _ ->
            dialog.cancel() //Cancel the dialog
        }
        alertDialog.show()
    }

    private fun addToFavorites(todo: Todo) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Add to Favorites...")
        alertDialog.setMessage("Are you sure you want to add this to favorites?")
        alertDialog.setIcon(R.drawable.baseline_star_border_24)
        alertDialog.setPositiveButton("YES") { _, _ ->
            dbHelper!!.addToFavorites(todo)
            getTodoList()  // refreshing the list
        }

        alertDialog.setNegativeButton("NO") { dialog, _ ->
            dialog.cancel() //Cancel the dialog
        }

        alertDialog.show()
    }

    /**
     * Shows alert dialog with EditText options to enter / edit  a note.
     * when shouldUpdate=true, it automatically displays old note and changes the  button text to UPDATE
     */
    @SuppressLint("SetTextI18n")
    private fun showNoteDialog(shouldUpdate: Boolean, todo: Todo?, position: Int) {
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.add_todo, null)

        val alertDialogView = AlertDialog.Builder(this).create()
        alertDialogView.setView(view)

        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val edTitle = view.findViewById<EditText>(R.id.edTitle)
        val edDesc = view.findViewById<EditText>(R.id.edDesc)
        val btAddUpdate = view.findViewById<Button>(R.id.btAddUpdate)
        val btCancel = view.findViewById<Button>(R.id.btCancel)
        if (shouldUpdate) btAddUpdate.text = "Update" else btAddUpdate.text = "Save"

        if (shouldUpdate && todo != null) {
            edTitle.setText(todo.title)
            edDesc.setText(todo.desc)
        }

        btAddUpdate.setOnClickListener(View.OnClickListener {
            val tName = edTitle.text.toString()
            val descName = edDesc.text.toString()

            if (TextUtils.isEmpty(tName)) {
                Toast.makeText(this, "Enter Your Title!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(descName)) {
                Toast.makeText(this, "Enter Your Description!", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            // check if user updating Todos
            if (shouldUpdate && todo != null) {
                updateNote(Todo(tName, descName), position)      // update note by it's id
            } else {
                createNote(Todo(tName, descName))   // create new note
            }
            alertDialogView.dismiss()
        })

        btCancel.setOnClickListener {
            alertDialogView.dismiss()
        }
        tvHeader.text =
            if (!shouldUpdate) getString(R.string.lbl_new_todo_title) else getString(R.string.lbl_edit_todo_title)

        alertDialogView.setCancelable(false)
        alertDialogView.show()
    }

    /**
     * Inserting new note in db and refreshing the list
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun createNote(todo: Todo) {
        val id =
            dbHelper!!.insertTodo(todo)    // inserting note in db and getting newly inserted note id
        val new = dbHelper!!.getTodo(id)  // get the newly inserted note from db
        todoList.add(0, new)    // adding new note to array list at 0 position
        myAdapter!!.notifyDataSetChanged()  // refreshing the list
    }

    /**
     * Updating note in db and updating item in the list by its position
     */
    private fun updateNote(t: Todo, position: Int) {
        val todo = todoList[position]
        todo.title = t.title    // updating title
        todo.desc = t.desc  // updating description
        dbHelper!!.updateTodo(todo) // updating note in db
        todoList[position] = todo  // refreshing the list
        myAdapter!!.notifyItemChanged(position)
    }
}
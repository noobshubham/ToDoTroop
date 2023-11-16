package org.eu.noobshubham.todotroop.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.eu.noobshubham.todotroop.model.Todo

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2  // Database Version
        private const val DATABASE_NAME = "remoteward_db" // Database Name
    }

    // Database Table Name
    private val TABLE_NAME = "todoTable"

    // Attributes for Tables
    private val COLUMN_ID = "id"
    private val COLUMN_TITLE = "title"
    private val COLUMN_DESCRIPTION = "description"
    private val COLUMN_TIMESTAMP = "timestamp"
    private val COLUMN_FAVORITE = "favorite"

    // Create table SQL query
    private val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_FAVORITE + " INTEGER DEFAULT 0"
                    + ")")

    // 1) Select All Query, 2)looping through all rows and adding to list 3) close db connection, 4) return todos list
    val allNotes: ArrayList<Todo>
        @SuppressLint("Range")
        get() {
            val notes = ArrayList<Todo>()
            val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $COLUMN_TIMESTAMP DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val todo = Todo(
                        cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE))
                    )
                    todo.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    todo.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    todo.desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                    todo.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                    todo.favourite = cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE))
                    notes.add(todo)
                } while (cursor.moveToNext())
            }
            db.close()
            return notes
        }

    val getAllFavourites: ArrayList<Todo>
        @SuppressLint("Range")
        get() {
            val notes = ArrayList<Todo>()
            val selectQuery =
                "SELECT  * FROM $TABLE_NAME WHERE $COLUMN_FAVORITE = 1 ORDER BY $COLUMN_TIMESTAMP DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val todo = Todo(
                        cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE))
                    )
                    todo.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    todo.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    todo.desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                    todo.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                    todo.favourite = cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE))
                    notes.add(todo)
                } while (cursor.moveToNext())
            }
            db.close()
            return notes
        }

    // return count
    val notesCount: Int
        get() {
            val countQuery = "SELECT  * FROM $TABLE_NAME"
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }

    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)  // create notes table
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // Drop older table if existed
        onCreate(db)  // Create tables again
    }

    fun insertTodo(todo: Todo): Long {
        val db = this.writableDatabase   // get writable database as we want to write data
        val values = ContentValues()
        // `id` and `timestamp` will be inserted automatically, no need to add them
        values.put(COLUMN_TITLE, todo.title)
        values.put(COLUMN_DESCRIPTION, todo.desc)
        val id = db.insert(TABLE_NAME, null, values)  // insert row
        db.close() // close db connection

        return id  // return newly inserted row id
    }

    @SuppressLint("Range")
    fun getTodo(id: Long): Todo {
        val db = this.readableDatabase    // get readable database as we are not inserting anything
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_TIMESTAMP, COLUMN_FAVORITE),
            "$COLUMN_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        cursor?.moveToFirst()
        // prepare todos object
        val todo = Todo(
            cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
            cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE))
        )

        cursor.close()   // close the db connection
        return todo
    }

    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, todo.title)
        values.put(COLUMN_DESCRIPTION, todo.desc)
        return db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(todo.id.toString())
        )   // updating row
    }

    fun deleteTodo(todo: Todo): Boolean {
        val db = writableDatabase   // Gets the data repository in write mode
        db.delete(
            TABLE_NAME,
            "$COLUMN_ID LIKE ?",
            arrayOf(todo.id.toString())
        ) // Issue SQL statement.
        return true
    }

    fun addToFavorites(todo: Todo) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FAVORITE, 1)
        db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(todo.id.toString())
        )   // updating row
    }
}
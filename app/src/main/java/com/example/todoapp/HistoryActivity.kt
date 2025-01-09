package com.example.todoapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private val list = arrayListOf<TodoModel>()
    private val adapter = TodoAdapter(list)

    private val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Setup RecyclerView
        val historyRv = findViewById<RecyclerView>(R.id.historyRv)
        historyRv.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }

        // Load completed tasks
        loadCompletedTasks()
    }

    private fun loadCompletedTasks() {
        db.todoDao().getCompletedTask().observe(this, Observer { tasks ->
            if (tasks.isNotEmpty()) {
                findViewById<View>(R.id.emptyView).visibility = View.GONE
                list.clear()
                list.addAll(tasks)
                adapter.notifyDataSetChanged()
            } else {
                findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
                list.clear()
                adapter.notifyDataSetChanged()
            }
            println("Current completed tasks: ${tasks.size}")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu1, menu) // Inflate the menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.action_clear_history -> {
                println("Clear History clicked") // Debug log
                clearHistory()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearHistory() {
        GlobalScope.launch(Dispatchers.IO) {
            val rowsDeleted = db.todoDao().deleteAllCompletedTasks()
            withContext(Dispatchers.Main) {
                if (rowsDeleted > 0) {
                    list.clear()
                    adapter.notifyDataSetChanged()
                    findViewById<View>(R.id.emptyView).visibility = View.VISIBLE
                    println("History cleared: $rowsDeleted rows deleted")
                    Toast.makeText(this@HistoryActivity, "History cleared!", Toast.LENGTH_SHORT).show()
                } else {
                    println("No history to clear")
                    Toast.makeText(this@HistoryActivity, "No history to clear!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

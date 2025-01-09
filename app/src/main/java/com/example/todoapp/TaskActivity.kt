package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TimePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val DB_NAME="todo.db"
class TaskActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myCalendar: Calendar
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private val labels = arrayListOf("Personal", "Business", "Insurance", "Shopping", "Banking")
    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        myCalendar = Calendar.getInstance() // Initialize calendar

        val saveBtn = findViewById<MaterialButton>(R.id.saveBtn)
        val dateEdt = findViewById<TextInputEditText>(R.id.dateEdt)
        val timeEdt = findViewById<TextInputEditText>(R.id.timeEdt)

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        setUpSpinner()
    }

    private fun setUpSpinner() {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels)
        labels.sort()
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        spinnerCategory.adapter = adapter
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dateEdt -> {
                setListener()
            }
            R.id.timeEdt -> {
                setTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()
            }
        }
    }

    private fun saveTodo() {
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val taskInpLay = findViewById<TextInputLayout>(R.id.taskInpLay)
        val titleInpLay = findViewById<TextInputLayout>(R.id.titleInpLay)

        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()

        val task = TodoModel(
            title = title,
            description = description,
            category = category,
            date = myCalendar.timeInMillis,  // Keep the milliseconds for the database
            time = myCalendar.timeInMillis   // Keep the milliseconds for the database
        )

        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                db.todoDao().insertTask(task)
            }
            saveToFirestore(task, id)
        }
    }

    private fun saveToFirestore(task: TodoModel, localId: Long) {
        val taskMap = hashMapOf(
            "title" to task.title,
            "description" to task.description,
            "category" to task.category,
            "date" to selectedDate,    // Store formatted date string
            "time" to selectedTime,    // Store formatted time string // Store milliseconds for sorting/querying
            "localId" to localId
        )

        firestore.collection("tasks")
            .add(taskMap)
            .addOnSuccessListener { documentReference ->
                println("Task added to Firestore with ID: ${documentReference.id}")
                finish()
            }
            .addOnFailureListener { e ->
                println("Error adding task to Firestore: $e")
                finish()
            }
    }

    private fun setTimeListener() {
        timeSetListener = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, min: Int ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            myCalendar.set(Calendar.MINUTE, min)
            myCalendar.set(Calendar.SECOND, 0)
            updateTime()
        }

        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            true  // Changed to true for 24-hour format
        )
        timePickerDialog.show()
    }

    private fun updateTime() {
        val myFormat = "HH:mm:ss"  // 24-hour format with seconds
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val timeEdt = findViewById<TextInputEditText>(R.id.timeEdt)
        selectedTime = sdf.format(myCalendar.time)
        timeEdt.setText(selectedTime)
    }

    private fun setListener() {
        dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        val myFormat = "yyyy-MM-dd"  // Changed to requested format
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val dateEdt = findViewById<TextInputEditText>(R.id.dateEdt)
        selectedDate = sdf.format(myCalendar.time)
        dateEdt.setText(selectedDate)

        val timeInptlay = findViewById<TextInputLayout>(R.id.timeInpLay)
        timeInptlay.visibility = View.VISIBLE
    }
}
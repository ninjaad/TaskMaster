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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

const val DB_NAME="todo.db"
class  TaskActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var myCalander:Calendar

    lateinit var dateSetListener:DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener

    var finalDate=0L
    var finalTime=0L

    private val labels= arrayListOf("Personal","Business","Insurance","Shopping","Banking")
    val db by lazy {
             AppDatabase.getDatabase(this)
     }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val saveBtn = findViewById<MaterialButton>(R.id.saveBtn)
        saveBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }


            val dateEdt=findViewById<TextInputEditText>(R.id.dateEdt)
        val timeEdt=findViewById<TextInputEditText>(R.id.timeEdt)

        dateEdt.setOnClickListener(this)
        timeEdt.setOnClickListener(this)
        saveBtn.setOnClickListener(this)


        setUpSinner()


    }


    private fun setUpSinner() {
      val adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)
        labels.sort()
        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategory)

        spinnerCategory.adapter=adapter
    }

    override fun onClick(v: View) {
        when(v.id){
          R.id.dateEdt->{
              SetListener()
            }
            R.id.timeEdt->{
                SetTimeListener()
            }
            R.id.saveBtn -> {
                saveTodo()
            }
        }
    }
    private fun saveTodo() {
        val spinnerCategory=findViewById<Spinner>(R.id.spinnerCategory)
        val taskInpLay=findViewById<TextInputLayout>(R.id.taskInpLay)
        val titleInpLay=findViewById<TextInputLayout>(R.id.titleInpLay)
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()


        GlobalScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                return@withContext db.todoDao().insertTask(
                    TodoModel(
                        title,
                        description,
                        category,
                        finalDate,
                        finalTime
                    )
                )
            }
            finish()
        }

    }

    private fun SetTimeListener() {
        myCalander= Calendar.getInstance()

        timeSetListener= TimePickerDialog.OnTimeSetListener(){ _: TimePicker, hourOfDay: Int, min: Int,  ->
            myCalander.set(Calendar.HOUR_OF_DAY,hourOfDay)
            myCalander.set(Calendar.MINUTE,min)
            updateTime()
        }

        val timePickerDialog=TimePickerDialog(
            this,
            timeSetListener,
            myCalander.get(Calendar.HOUR_OF_DAY),
            myCalander.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateTime() {
        //Mon, 5 Jan 2020
        val myFormat= "h:mm a"
        val sdf=SimpleDateFormat(myFormat)
        val timeEdt=findViewById<TextInputEditText>(R.id.timeEdt)

        timeEdt.setText(sdf.format(myCalander.time))


    }

    private fun SetListener() {
        myCalander= Calendar.getInstance()

        dateSetListener= DatePickerDialog.OnDateSetListener{ _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            myCalander.set(Calendar.YEAR,year)
            myCalander.set(Calendar.MONTH,month)
            myCalander.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDate()
        }

        val datePickerDialog=DatePickerDialog(
            this,
            dateSetListener,
            myCalander.get(Calendar.YEAR),
            myCalander.get(Calendar.MONTH),
            myCalander.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate=System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        //Mon, 5 Jan 2020
        val myFormat= "EEE, d MMM yyyy"
        val sdf=SimpleDateFormat(myFormat)
        val dateEdt=findViewById<TextInputEditText>(R.id.dateEdt)

        dateEdt.setText(sdf.format(myCalander.time))

        val timeInptlay=findViewById<TextInputLayout>(R.id.timeInpLay)
        timeInptlay.visibility= View.VISIBLE
    }
}
package com.example.todoapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    val list= arrayListOf<TodoModel>()
    var adapter=TodoAdapter(list)

    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val floating = findViewById<FloatingActionButton>(R.id.floating)
        floating.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            val todoRv = findViewById<RecyclerView>(R.id.todoRv)



            todoRv.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = this@MainActivity.adapter

            }
            initSwipe()


            db.todoDao().getTask().observe(this, Observer {
                if (!it.isNullOrEmpty()) {
                    list.clear()
                    list.addAll(it)
                    adapter.notifyDataSetChanged()
                }
                else{
                    list.clear()
                    adapter.notifyDataSetChanged()
                }
            })


        }
    }

        fun initSwipe(){
            val simpleItemTouchCallback=object: ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean =false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    if(direction==ItemTouchHelper.LEFT)
                    {
                        GlobalScope.launch(Dispatchers.IO) {
                            db.todoDao().deleteTask(adapter.getItemId(position))

                        }

                    }else if(direction== ItemTouchHelper.RIGHT){
                        GlobalScope.launch(Dispatchers.IO) {
                            db.todoDao().finishTask(adapter.getItemId(position))

                        }
                    }
                }


                override fun onChildDraw(
                    canvas: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE)
                    {
                        val itemView =viewHolder.itemView

                        val paint =Paint()
                        val icon:Bitmap

                        if(dX > 0)
                        {
                            icon=BitmapFactory.decodeResource(resources,R.mipmap.check)

                            paint.color=Color.parseColor("#4CAF50")

                            canvas
                                .drawRect(
                                    itemView.left.toFloat(),itemView.top.toFloat(),
                                    itemView.left.toFloat()+dX,itemView.bottom.toFloat(),paint
                                )

                            canvas.drawBitmap(
                                icon,
                                itemView.left.toFloat(),
                                itemView.top.toFloat() + (itemView.bottom.toFloat()-itemView.top.toFloat()-icon.height.toFloat())/2,
                                 paint

                            )

                        }
                        else{
                            icon=BitmapFactory.decodeResource(resources,R.mipmap.delete)

                            paint.color=Color.parseColor("#F44336")

                            canvas
                                .drawRect(
                                    itemView.right.toFloat()+dX,itemView.top.toFloat(),
                                    itemView.right.toFloat(),itemView.bottom.toFloat(),paint
                                )

                            canvas.drawBitmap(
                                icon,
                                itemView.right.toFloat()-icon.width,
                                itemView.top.toFloat() + (itemView.bottom.toFloat()-itemView.top.toFloat()-icon.height.toFloat())/2,
                                paint

                            )
                        }

                        viewHolder.itemView.translationX=dX
                    }
                    else {
                        super.onChildDraw(
                            canvas,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                }


            }
            val todoRv=findViewById<RecyclerView>(R.id.todoRv)
            val itemTouchHelper=ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(todoRv)
        }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
     menuInflater.inflate(R.menu.main_menu,menu)
        val item=menu.findItem(R.id.search)
        val searchView=item.actionView as SearchView
        item.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
              return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }
    fun displayTodo(newText:String=" "){
        db.todoDao().getTask().observe(this, Observer {
            if (it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter {todo ->
                    todo.title.contains(newText,true)

                    }
                )
                adapter.notifyDataSetChanged()
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history->{
                startActivity(Intent(this,HistoryActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
        fun openNewTask(view: View) {
            startActivity(Intent(this, TaskActivity::class.java))
        }
    }

}
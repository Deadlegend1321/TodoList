package com.example.todo

import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val list = arrayListOf<TodoModel>()
    var adapter = TodoAdapter(list)

    val db by lazy {
        AppDatabase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        todoRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        initSwipe()
        db.todoDao().getTask().observe(this, Observer {
            if (!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
    fun initSwipe(){
        val simpleItemTouchCallBack = object : ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT)
                {
                    db.todoDao().deleteTask(adapter.getItemId(position))
                }
                else if (direction == ItemTouchHelper.RIGHT){
                    db.todoDao().finishTask(adapter.getItemId(position))
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView
                    val paint = Paint()
                    val icon:Bitmap
                    if (dX>0)
                    {
                        icon =BitmapFactory.decodeResource(resources,R.mipmap.ic_check_white)
                        paint.color = Color.parseColor("#388E3C")
                        c.drawRect(itemView.left.toFloat(),itemView.top.toFloat(),
                        itemView.left.toFloat() + dX,itemView.bottom.toFloat(),paint)
                        c.drawBitmap(icon,itemView.left.toFloat(),itemView.top.toFloat()
                            + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat())/2,paint)
                    }
                    
                    viewHolder.itemView.translationX = dX
                }
                else {
                    super.onChildDraw(
                        c,
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
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(todoRv)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history ->{
                startActivity(Intent(this,HistoryActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun openNewTask(view: View){
        startActivity(Intent(this,TaskActivity::class.java))
    }
}
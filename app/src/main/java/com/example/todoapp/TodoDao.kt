package com.example.todoapp

import kotlinx.coroutines.Deferred
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TodoDao {
    @Insert
    fun insertTask(todoModel: TodoModel):Long

    @Query("Select * from TodoModel where isFinished == 0")
    fun getTask(): LiveData<List<TodoModel>>

    @Query("Update TodoModel set isFinished= 1 where id=:uid")
    fun finishTask(uid: Long)

    @Query("Delete from TodoModel where id=:uid")
    fun deleteTask(uid: Long)

    @Query("SELECT * FROM TodoModel WHERE isFinished = 1")
    fun getCompletedTask(): LiveData<List<TodoModel>>

    @Query("DELETE FROM TodoModel WHERE isFinished = 1")
    fun deleteAllCompletedTasks(): Int


}

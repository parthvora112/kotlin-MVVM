package com.teco.apparchitecture.room

import androidx.room.*
import com.teco.apparchitecture.model.ToDo
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_table")
    fun getToDos(): Flow<List<ToDo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToDo(toDo: ToDo)

    @Update
    suspend fun update(todo: ToDo)
}
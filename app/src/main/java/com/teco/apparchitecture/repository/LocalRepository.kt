package com.teco.apparchitecture.repository

import com.teco.apparchitecture.model.ToDo
import com.teco.apparchitecture.room.ToDoDao
import javax.inject.Inject

class LocalRepository @Inject constructor(
    private val toDoDao: ToDoDao
) {
    fun getAllToDos() = toDoDao.getToDos()

    suspend fun addToDo(toDo: ToDo) = toDoDao.addToDo(toDo)

    suspend fun updateToDo(todo: ToDo) {
        toDoDao.update(todo)
    }
}
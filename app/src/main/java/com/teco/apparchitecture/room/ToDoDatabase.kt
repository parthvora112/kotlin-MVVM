package com.teco.apparchitecture.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teco.apparchitecture.model.ToDo

@Database(entities = [ToDo::class], version = 1)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun getToDoDao(): ToDoDao
}
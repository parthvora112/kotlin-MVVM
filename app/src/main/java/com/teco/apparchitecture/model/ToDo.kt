package com.teco.apparchitecture.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
 data class ToDo @JvmOverloads constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String? = null,
    var description: String? = null,
    var isCompleted: Boolean? = null,
    var isExpand: Boolean? = false
) {

}


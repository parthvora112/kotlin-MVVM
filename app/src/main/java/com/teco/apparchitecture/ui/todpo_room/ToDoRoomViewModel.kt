package com.teco.apparchitecture.ui.todpo_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.teco.apparchitecture.model.ToDo
import com.teco.apparchitecture.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToDoRoomViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {

    fun getAllTasks() = localRepository.getAllToDos().asLiveData()

    fun updateToDo(todo: ToDo) {
        viewModelScope.launch {
            localRepository.updateToDo(todo)
        }
    }
}
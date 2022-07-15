package com.teco.apparchitecture.ui.add_edit_todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teco.apparchitecture.model.ToDo
import com.teco.apparchitecture.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditToDoViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {

    fun addTodo(toDo: ToDo) {
        viewModelScope.launch {
            localRepository.addToDo(toDo)
        }
    }

}
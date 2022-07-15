package com.teco.apparchitecture.ui.todpo_room

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.teco.apparchitecture.R
import com.teco.apparchitecture.adapter.TodoAdapter
import com.teco.apparchitecture.databinding.FragmentToDoRoomBinding
import com.teco.apparchitecture.model.ToDo
import com.teco.apparchitecture.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ToDoRoomFragment : BaseFragment<FragmentToDoRoomBinding>(),
    TodoAdapter.OnToDoItemClickListener {

    private var todoList: List<ToDo> = emptyList()
    private val viewModel: ToDoRoomViewModel by viewModels()
    private lateinit var todoAdapter: TodoAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentToDoRoomBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentToDoRoomBinding.inflate(layoutInflater, viewGroup, isAttachToParent)


    override fun viewCreated(view: View) {
        initViews()
        initObserver()
    }

    private fun initObserver() {
        viewModel.getAllTasks().observe(
            viewLifecycleOwner
        ) {
            Log.e(TAG, "initObserver: Called :: $it")
            todoList = it
            todoAdapter.submitList(it)

        }
    }

    private fun initViews() {
        binding.apply {
            fabAddTodo.setOnClickListener {
                navigateToFragment(R.id.action_toDoRoomFragment_to_addEditTodoFragment)
            }
            todoAdapter = TodoAdapter(this@ToDoRoomFragment)
            rvTodos.layoutManager = LinearLayoutManager(requireContext())
            rvTodos.adapter = todoAdapter
        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}
    override fun onToDoItemExpandCollapseClick(todo: ToDo) {
        viewModel.updateToDo(todo.apply {
            isExpand = !isExpand!!
        })
    }

}
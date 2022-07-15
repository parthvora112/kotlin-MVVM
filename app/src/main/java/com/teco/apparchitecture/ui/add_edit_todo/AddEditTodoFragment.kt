package com.teco.apparchitecture.ui.add_edit_todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.teco.apparchitecture.databinding.FragmentAddEditTodoBinding
import com.teco.apparchitecture.model.ToDo
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppUtil.showSnackMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTodoFragment : BaseFragment<FragmentAddEditTodoBinding>() {

    private val viewModel: AddEditToDoViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddEditTodoBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentAddEditTodoBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        (activity as AppCompatActivity).supportActionBar?.title = "Add Task"
        initViews()
    }

    private fun initViews() {
        binding.fabAddTodo.setOnClickListener {
            if(validInput()){
                viewModel.addTodo(
                    ToDo(
                        title = binding.etTaskTitle.text.toString(),
                        description = binding.etTaskDesc.text.toString()
                    )
                )
                onBackPressed()
            }
        }
    }

    private fun validInput(): Boolean {
        var isValidAll = true
        if(binding.etTaskTitle.text?.toString()?.isEmpty()!!){
            showSnackMessage(
                requireContext(),
                binding.root,
                "Please add task title."
            )
            isValidAll = false
        }
        return isValidAll
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

}
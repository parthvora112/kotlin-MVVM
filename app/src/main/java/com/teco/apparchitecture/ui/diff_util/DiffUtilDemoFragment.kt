package com.teco.apparchitecture.ui.diff_util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.teco.apparchitecture.adapter.DiffUtilAdapter
import com.teco.apparchitecture.adapter.DiffUtilListAdapter
import com.teco.apparchitecture.databinding.FragmentDiffUtilDemoBinding
import com.teco.apparchitecture.model.DiffUtilDataModel
import com.teco.apparchitecture.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DiffUtilDemoFragment : BaseFragment<FragmentDiffUtilDemoBinding>(),
    DiffUtilAdapter.DiffUtilItemClickListener {

    private lateinit var diffUtilAdapter: DiffUtilAdapter
    private lateinit var diffUtilListAdapter: DiffUtilListAdapter
    private val viewModel: DiffUtilDemoViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDiffUtilDemoBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentDiffUtilDemoBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        binding.apply {
            rvDiffUtil.layoutManager = LinearLayoutManager(requireContext())
            diffUtilAdapter = DiffUtilAdapter(this@DiffUtilDemoFragment)
            diffUtilListAdapter = DiffUtilListAdapter(this@DiffUtilDemoFragment)
            rvDiffUtil.setHasFixedSize(true)
//            rvDiffUtil.adapter = diffUtilAdapter
            rvDiffUtil.adapter = diffUtilListAdapter
//            diffUtilAdapter.differ.submitList(viewModel.itemList.toMutableList())
            diffUtilListAdapter.submitList(viewModel.itemList.toMutableList())

        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun onDiffUtilItemStatusClick(item: DiffUtilDataModel) {

//        viewModel.itemList.remove(item)


        val index = viewModel.itemList.indexOf(item)
//        newList[index] = viewModel.itemList[index].copy(activeStatus = !item.activeStatus!!)
        diffUtilListAdapter.submitList(viewModel.itemList.toList().toMutableList().let {
            it[index] = it[index].copy(activeStatus = !item.activeStatus!!) // To update a property on an item
            viewModel.itemList = it
            it
        })
    }
}
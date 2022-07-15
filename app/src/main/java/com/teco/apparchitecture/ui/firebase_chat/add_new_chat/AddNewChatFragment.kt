package com.teco.apparchitecture.ui.firebase_chat.add_new_chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.teco.apparchitecture.R
import com.teco.apparchitecture.adapter.NewChatUserAdapter
import com.teco.apparchitecture.databinding.FragmentAddNewChatBinding
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddNewChatFragment : BaseFragment<FragmentAddNewChatBinding>(),
    NewChatUserAdapter.NewChatUserItemClickLister {

    private val viewModel: AddNewChatViewModel by viewModels()
    private lateinit var userListAdapter: NewChatUserAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddNewChatBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentAddNewChatBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        initViews()
        initObserver()
        viewModel.fetchAllUsers()
    }

    private fun initViews() {
        binding.apply {
            userListAdapter = NewChatUserAdapter(this@AddNewChatFragment)
            rvUsers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = userListAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        requireActivity(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.addNewChatUiState.collect {
                when (it) {
                    AddNewChatViewModel.AddNewChatUiState.Empty -> {
                        binding.progressCircular.isVisible = false
                    }
                    is AddNewChatViewModel.AddNewChatUiState.Error -> {
                        binding.progressCircular.isVisible = false
                        showSnackMessage(it.errorMessage ?: getString(R.string.something_wrong))
                    }
                    AddNewChatViewModel.AddNewChatUiState.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    is AddNewChatViewModel.AddNewChatUiState.Success -> {
                        binding.progressCircular.isVisible = false
                        userListAdapter.addUsersList(it.users)
                    }
                }
            }
        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun onUserItemClick(firebaseAppUser: FirebaseAppUser) {
        Log.e(TAG, "onUserItemClick: $firebaseAppUser")
        navigateToFragment(R.id.action_addNewChatFragment_to_singleChatFragment,
            Bundle().apply { putParcelable(AppConstants.KEY_CHAT_USER, firebaseAppUser) })
    }


}
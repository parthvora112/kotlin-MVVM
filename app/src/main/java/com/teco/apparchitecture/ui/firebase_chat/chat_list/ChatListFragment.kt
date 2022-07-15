package com.teco.apparchitecture.ui.firebase_chat.chat_list

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.teco.apparchitecture.R
import com.teco.apparchitecture.adapter.ChatListAdapter
import com.teco.apparchitecture.databinding.FragmentChatListBinding
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(),
    ChatListAdapter.ChatListItemClickListener {

    private val viewModel: ChatListViewModel by viewModels()

    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<FirebaseAppUser>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatListBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentChatListBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        initViews()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.chatListUiState.collect { it ->
                when (it) {
                    ChatListViewModel.ChatListUiState.Empty -> {

                    }
                    is ChatListViewModel.ChatListUiState.NewChatFound -> {
                        Log.e(TAG, "initObservers: ${it.firebaseAppUser}")
                        if (!chatList.contains(it.firebaseAppUser))
                            chatList.add(it.firebaseAppUser)
                        else {
                            val index = chatList.indexOf(it.firebaseAppUser)
                            chatList.removeAt(index)
                            chatList.add(index, it.firebaseAppUser)
                        }
                        chatList.sortByDescending { it.lastMessage?.timeStamp }
                        chatListAdapter.submitList(chatList.toMutableList())
                    }
                }
            }
        }
    }

    private fun initViews() {
        binding.apply {
            rvChatList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                chatListAdapter = ChatListAdapter(this@ChatListFragment)
                adapter = chatListAdapter
                setHasFixedSize(true)
                addItemDecoration(
                    DividerItemDecoration(
                        requireActivity(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
            btnAddNewChat.setOnClickListener {
                navigateToFragment(R.id.action_chatListFragment_to_addNewChatFragment)
            }
        }
        setHasOptionsMenu(true)
            viewModel.fetchUserChatsList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chat_list, menu)
        val menuItem = menu.findItem(R.id.menu_search_chat_list)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?) = false

            override fun onQueryTextChange(query: String?): Boolean {
                searchInList(query)
                return false
            }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.menu_logout -> {
               Log.e(TAG, "onOptionsItemSelected: R.id.menu_logout")
               viewModel.logoutUser()
               navigateToFragment(R.id.action_chatListFragment_to_homeFragment   )
               return true
           }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchInList(query: String?) {
        if (query == null) return
        val chatList = if (query.isEmpty()) {
            chatList
        } else {
            chatList.filter {
                it.name?.contains(query, true)!!
            }
        }
        chatListAdapter.submitList(chatList.toMutableList())
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun onChatListItemClick(firebaseAppUser: FirebaseAppUser) {
//        navigateToFragment(
//            R.id.action_chatListFragment_to_singleChatFragment,
//            Bundle().apply { putParcelable(KEY_CHAT_LIST_USER, viewModel.tempChatList[position]) })
        navigateToFragment(R.id.action_chatListFragment_to_singleChatFragment,
            Bundle().apply { putParcelable(AppConstants.KEY_CHAT_USER, firebaseAppUser) })
    }

}
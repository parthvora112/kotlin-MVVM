package com.teco.apparchitecture.ui.firebase_chat.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.teco.apparchitecture.R
import com.teco.apparchitecture.adapter.SingleChatMessageAdapter
import com.teco.apparchitecture.databinding.FragmentSingleChatBinding
import com.teco.apparchitecture.databinding.ItemListChatMessageBinding
import com.teco.apparchitecture.model.FirebaseAppUser
import com.teco.apparchitecture.model.Message
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppConstants.KEY_CHAT_USER
import com.teco.apparchitecture.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class SingleChatFragment : BaseFragment<FragmentSingleChatBinding>() {

    private val viewModel: SingleChatViewModel by viewModels()

    private lateinit var ivUserProfile: AppCompatImageView
    private lateinit var tvUserName: AppCompatTextView
    private lateinit var chatAdapter: SingleChatMessageAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSingleChatBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentSingleChatBinding.inflate(
            layoutInflater,
            viewGroup,
            isAttachToParent
        )

    override fun viewCreated(view: View) {
        initArguments()
        initViews()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.singleChatUiState.collect {
                when (it) {
                    SingleChatViewModel.SingleChatUiState.Empty -> {}
                    is SingleChatViewModel.SingleChatUiState.Success -> {
                        initAdapterForMessage(it.fireStoreQuery)
                    }
                }
            }
        }
        viewModel.initFirebaseChat()
    }

    private fun initAdapterForMessage(collection: CollectionReference) {
        val options: FirestoreRecyclerOptions<Message> =
            FirestoreRecyclerOptions.Builder<Message>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(
                    collection.orderBy("timeStamp", Query.Direction.ASCENDING), Message::class.java
                )
                .build()

        chatAdapter = SingleChatMessageAdapter(
            viewModel.currentUserId,
            options
        )

        chatAdapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(
                    positionStart: Int,
                    itemCount: Int
                ) {
                    binding.rvMessage.smoothScrollToPosition(positionStart)
                    super.onItemRangeInserted(positionStart, itemCount)
                }

                override fun onChanged() {
                }
            }
        )
        binding.rvMessage.adapter = chatAdapter
    }

    override fun onResume() {
        super.onResume()
        if (this::ivUserProfile.isInitialized) ivUserProfile.isVisible = true
        if (this::tvUserName.isInitialized) tvUserName.isVisible = true
    }

    private fun initViews() {
        val toolBar = (activity as AppCompatActivity).findViewById<Toolbar>(R.id.tool_bar)
        ivUserProfile = toolBar.findViewById(R.id.iv_user_profile)
        tvUserName = toolBar.findViewById(R.id.tv_user_name)
        ivUserProfile.isVisible = true
        tvUserName.isVisible = true

        viewModel.firebaseChatUser?.let {
            tvUserName.text = it.name
            it.profile?.let { image ->
                Glide.with(binding.root).load(AppUtil.decodeImage(image))
                    .placeholder(R.drawable.img_user_placeholder).circleCrop()
                    .into(ivUserProfile)
            }
        }

        binding.apply {
            rvMessage.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            ivSend.setOnClickListener {
                viewModel.sendMessage(binding.etMessage.text.toString().trim())
                binding.etMessage.setText("")
            }
        }
    }

    private fun initArguments() {
        if (arguments == null) return
        viewModel.firebaseChatUser = requireArguments().get(KEY_CHAT_USER) as FirebaseAppUser?
    }

    override fun onStart() {
        super.onStart()
        if (this::chatAdapter.isInitialized)
            chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::chatAdapter.isInitialized)
            chatAdapter.stopListening()
    }


    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun onPause() {
        super.onPause()
        ivUserProfile.isVisible = false
        tvUserName.isVisible = false
    }


    class SingleChatViewHolder(private val binding: ItemListChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Message) {
            binding.tvMessage.text = model.message
        }

    }

}
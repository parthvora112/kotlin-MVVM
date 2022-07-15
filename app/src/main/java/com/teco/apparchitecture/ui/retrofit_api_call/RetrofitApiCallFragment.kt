package com.teco.apparchitecture.ui.retrofit_api_call

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.teco.apparchitecture.adapter.PostsAdapter
import com.teco.apparchitecture.databinding.FragmentRetrofitApiCallBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppUtil.showSnackMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RetrofitApiCallFragment : BaseFragment<FragmentRetrofitApiCallBinding>() {

    private val viewModel: RetrofitApiCallViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRetrofitApiCallBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentRetrofitApiCallBinding.inflate(layoutInflater, viewGroup, isAttachToParent)

    override fun viewCreated(view: View) {
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            delay(2000)
            viewModel.getPost()
            viewModel.getPostStateFlow.collect {
                when(it){
                    RetrofitApiCallViewModel.ApiState.Empty -> {
                        binding.progressCircular.isVisible = false
                    }
                    is RetrofitApiCallViewModel.ApiState.Failure -> {
                        binding.progressCircular.isVisible = false
                        Log.e(TAG, "viewCreated: Error ${it.msg.localizedMessage}")
                        showSnackMessage(
                            context,
                            binding.root,
                            it.msg.localizedMessage
                        )
                    }
                    RetrofitApiCallViewModel.ApiState.Loading -> {
                        binding.progressCircular.isVisible = true
                    }
                    is RetrofitApiCallViewModel.ApiState.Success -> {
                        binding.progressCircular.isVisible = false
                        binding.rvPosts.adapter = PostsAdapter(it.data)
                    }
                }
            }
        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}


}
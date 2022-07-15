package com.teco.apparchitecture.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.teco.apparchitecture.R
import com.teco.apparchitecture.adapter.OptionsAdapter
import com.teco.apparchitecture.databinding.FragmentHomeBinding
import com.teco.apparchitecture.ui.BaseFragment
import com.teco.apparchitecture.util.AppUtil
import com.teco.apparchitecture.util.AppUtil.convertDpToPixel
import com.teco.apparchitecture.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), OptionsAdapter.OnOptionItemClickListener {

    private val viewModel: HomeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = fun(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            isAttachToParent: Boolean
        ) = FragmentHomeBinding.inflate(layoutInflater, viewGroup, isAttachToParent)

    override fun viewCreated(view: View) {
        initViews()
    }

    private fun initViews() {
        activity?.title = "your title"
        binding.rvOptions.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(
                GridSpacingItemDecoration(
                    2,
                    convertDpToPixel(5F, requireContext()),
                    true
                )
            )
            setHasFixedSize(true)
            adapter = OptionsAdapter(viewModel.optionsList, this@HomeFragment)
        }
    }

    override fun onStartApplication() {}

    override fun onPauseApplication() {}

    override fun onOptionItemClick(optionType: AppUtil.OptionType) {
        when(optionType){
            AppUtil.OptionType.TYPE_FORM_VALIDATION -> {
                navigateToFragment(R.id.action_homeFragment_to_formValidationFragment)
            }
            AppUtil.OptionType.TYPE_API_CALL -> {
                navigateToFragment(R.id.action_homeFragment_to_retrofitApiCallFragment)
            }
            AppUtil.OptionType.TYPE_DATA_BASE -> {
                navigateToFragment(R.id.action_homeFragment_to_toDoRoomFragment)
            }
            AppUtil.OptionType.TYPE_CLOUD_DATA_BASE -> {
                if(viewModel.firebaseUser != null){
                    navigateToFragment(R.id.action_homeFragment_to_editProfileFragment)
                }else{
                    navigateToFragment(R.id.action_homeFragment_to_loginFragment)
                }
            }
            AppUtil.OptionType.TYPE_DIFF_UTILS -> {
                navigateToFragment(R.id.action_homeFragment_to_diffUtilDemoFragment)
            }
        }
    }

}
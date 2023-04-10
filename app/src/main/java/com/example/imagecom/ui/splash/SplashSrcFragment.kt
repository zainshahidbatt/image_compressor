package com.example.imagecom.ui.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.fragment.findNavController
import com.example.imagecom.R
import com.example.imagecom.databinding.FragmentSplashSrcBinding
import com.example.imagecom.ui.basefragment.BaseFragment


class SplashSrcFragment : BaseFragment<FragmentSplashSrcBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSplashSrcBinding =
        FragmentSplashSrcBinding::inflate

    override fun onCreatedView() {
        startAnimation()
        binding.apply {
            layoutClick.setOnClickListener {
                val action =
                    SplashSrcFragmentDirections.actionSplashSrcFragmentToDashboardFragment()
                findNavController().navigate(action)
            }
        }

    }

    private fun startAnimation() {
        val animationZoomIn = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in)
        binding.layoutClick.startAnimation(animationZoomIn)
    }
}
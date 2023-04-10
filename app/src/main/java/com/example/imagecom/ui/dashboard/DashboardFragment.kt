package com.example.imagecom.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.imagecom.databinding.FragmentDashboardBinding
import com.example.imagecom.ui.basefragment.BaseFragment


class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDashboardBinding
        get() = FragmentDashboardBinding::inflate

    override fun onCreatedView() {
        binding.apply {
            btnCompress.setOnClickListener {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToCompressImageFragment()
                findNavController().navigate(action)
            }
            btnVideo.setOnClickListener {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToCompressVedioFragment()
                findNavController().navigate(action)
            }
            btnAbout.setOnClickListener {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToAboutDetailsFragment()
                findNavController().navigate(action)
            }
        }
    }

}
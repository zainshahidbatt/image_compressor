package com.example.imagecom.utils

import androidx.fragment.app.Fragment
import com.example.imagecom.R
import com.example.imagecom.databinding.DailogFileSaveBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.showBottomSheetDialog(
    action: () -> Unit,
) {
    BottomSheetDialog(requireContext(), R.style.SheetDialog).also { dialog ->
        val binding = DailogFileSaveBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.behavior.isDraggable = false
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            ivCross.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
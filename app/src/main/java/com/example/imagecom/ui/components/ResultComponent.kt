package com.example.imagecom.ui.components

import com.example.imagecom.databinding.ComponentSuccessfullBinding

class ResultComponent(
    binding: ComponentSuccessfullBinding,
    resultSize: String
) {
    init {
        binding.percentageTxt.text = resultSize
    }
}
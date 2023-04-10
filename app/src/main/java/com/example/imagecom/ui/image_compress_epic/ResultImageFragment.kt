package com.example.imagecom.ui.image_compress_epic

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.imagecom.databinding.FragmentResultImageBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import com.example.imagecom.ui.components.ResultComponent
import com.example.imagecom.utils.showBottomSheetDialog

class ResultImageFragment : BaseFragment<FragmentResultImageBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultImageBinding
        get() = FragmentResultImageBinding::inflate
    private val args by navArgs<ResultImageFragmentArgs>()

    private var originalSize: Long? = null
    private var compressedSizeCheck: Long? = null
    private var compressedImgUri: Uri? = null
    private var compressedSize: Long? = null
    private var sizeString: String? = null

    @SuppressLint("Recycle")
    override fun onCreatedView() {
        compressedImgUri = args.imgUri
        originalSize = args.fileSize

        showBottomSheetDialog {

        }

        val fileDescriptor: AssetFileDescriptor? =
            requireContext().contentResolver.openAssetFileDescriptor(compressedImgUri!!, "r")
        compressedSizeCheck = fileDescriptor!!.length
        binding.imgImage.setImageURI(compressedImgUri)

        if (compressedSizeCheck!! < 1000000) {
            compressedSize = compressedSizeCheck!! / 1000

            sizeString = compressedSize.toString() + "KB"

        } else if (compressedSizeCheck!! >= 1000000) {
            compressedSize = compressedSizeCheck!! / 1000000
            sizeString = compressedSize.toString() + "MB"
        }

        ResultComponent(
            binding = binding.successMessage,
            resultSize = sizeString!!
        )

        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnSave.setOnClickListener {
                shareImage(compressedImgUri!!)
            }
        }
    }

    private fun shareImage(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        intent.type = "image/png"

        startActivity(Intent.createChooser(intent, "Share Via"))
    }
}
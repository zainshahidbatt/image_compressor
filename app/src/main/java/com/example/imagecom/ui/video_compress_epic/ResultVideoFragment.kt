package com.example.imagecom.ui.video_compress_epic

import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.imagecom.databinding.FragmentResultVideoBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import com.example.imagecom.ui.components.ResultComponent
import com.example.imagecom.utils.showBottomSheetDialog


class ResultVideoFragment : BaseFragment<FragmentResultVideoBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResultVideoBinding
        get() = FragmentResultVideoBinding::inflate

    private val args by navArgs<ResultVideoFragmentArgs>()
    private var mediaController: MediaController? = null

    private var originalSize: Long? = null
    private var compressedSizeCheck: Long? = null
    private var compressedImgUri: Uri? = null
    private var compressedSize: Long? = null
    private var sizeString: String? = null

    override fun onCreatedView() {
        compressedImgUri = args.videoUri
        originalSize = args.fileSize
        playVideo()
        showBottomSheetDialog {
            shareVideo(compressedImgUri!!)
        }
        val fileDescriptor: AssetFileDescriptor? =
            requireContext().contentResolver.openAssetFileDescriptor(compressedImgUri!!, "r")
        compressedSizeCheck = fileDescriptor!!.length
        binding.imgImage.setVideoURI(compressedImgUri)

        if (compressedSizeCheck!! < 1000000) {
            compressedSize = compressedSizeCheck!! / 1000

            sizeString = compressedSize.toString() + "KB"

        } else if (compressedSizeCheck!! >= 1000000) {
            compressedSize = compressedSizeCheck!! / 1000000
            sizeString = compressedSize.toString() + "MB"
        }

        ResultComponent(
            binding = binding.detailSuccess,
            resultSize = sizeString!!
        )
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnSave.setOnClickListener {
                shareVideo(compressedImgUri!!)
            }
        }
    }

    private fun playVideo() {
        binding.apply {
            if (mediaController == null) {
                // create an object of media controller class
                mediaController = MediaController(requireActivity())
                mediaController!!.setAnchorView(binding.imgImage)
            }
            imgImage.setMediaController(mediaController)
            imgImage.start()
        }
    }

    private fun shareVideo(uri: Uri) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "application/video"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, "Share file"))
    }
}
package com.example.imagecom.ui.image_compress_epic

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.imagecom.databinding.FragmentCompressImageBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import java.io.IOException


@Suppress("DEPRECATION")
class CompressImageFragment : BaseFragment<FragmentCompressImageBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCompressImageBinding =
        FragmentCompressImageBinding::inflate

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private var actualImage: Uri? = null

    override fun onCreatedView() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            binding.cvUpload.root.setOnClickListener {
                chooseImage()
            }
        }
        requestAllPermission()
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!")
                return
            }
            try {
                actualImage = data.data
                val action =
                    CompressImageFragmentDirections.actionCompressImageFragmentToImageSelectedFragment(
                        actualImage!!
                    )
                findNavController().navigate(action)

            } catch (e: IOException) {
                showError("Failed to read picture data!")
                e.printStackTrace()
            }
        }
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
    }


    private fun requestAllPermission() {
        if (
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // request all permission need, just for demo
            // but in production, it should handle every single permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                101
            )
        }
    }
}



package com.example.imagecom.ui.video_compress_epic

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.imagecom.databinding.FragmentCompressVedioBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import timber.log.Timber


@Suppress("DEPRECATION", "SameParameterValue")
class CompressVedioFragment : BaseFragment<FragmentCompressVedioBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCompressVedioBinding
        get() = FragmentCompressVedioBinding::inflate

    companion object {
        const val PICK_IMAGE_REQ_CODE = 1
        const val STORAGE_PERMISSION_REQ_CODE = 12321
    }

    override fun onCreatedView() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            binding.cvUpload.root.setOnClickListener {
                checkPermission {
                    pickVideo()
                }
            }

        }
    }

    private fun checkPermission(onGranted: () -> Unit) {
        val readStorage =
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val writeStorage =
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (readStorage == PackageManager.PERMISSION_GRANTED && writeStorage == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_REQ_CODE
            )
        }
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
        }
        startActivityForResult(intent, PICK_IMAGE_REQ_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_PERMISSION_REQ_CODE -> {
                checkPermission {
                    pickVideo()
                }
            }
            else -> {
                toast("Request Code not found onRequestPermissionResult")
            }
        }
    }

    private fun log(msg: String) {
        Timber.tag("IniLogDoing").d("Msg: %s", msg)
    }

    private fun toast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQ_CODE -> {
                log("requestCode $requestCode, resultCode : $resultCode, data : ${data.toString()}")
                if (resultCode == RESULT_OK && data != null) {
                    val uriCompress = data.data
                    val action =
                        CompressVedioFragmentDirections.actionCompressVedioFragmentToVedioSelectedFragment(
                            uriCompress!!
                        )
                    findNavController().navigate(action)
                }
            }
            else -> {
                log("Request Code Not Found")
            }
        }
    }


}
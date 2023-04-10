package com.example.imagecom.ui.image_compress_epic

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.imagecom.databinding.FragmentImageSelectedBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
@SuppressLint("Recycle")
class ImageSelectedFragment : BaseFragment<FragmentImageSelectedBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentImageSelectedBinding
        get() = FragmentImageSelectedBinding::inflate

    private val args by navArgs<ImageSelectedFragmentArgs>()

    private var imageUri: Uri? = null
    private var imgSize: Long? = null
    private var fileSize: Long? = null


    @SuppressLint("SetTextI18n")
    override fun onCreatedView() {
        imageUri = args.imgUri

        val imageExtension =
            (DocumentFile.fromSingleUri(requireContext(), imageUri!!)?.name ?: "").split('.')[1]

        val fileDescriptor: AssetFileDescriptor? =
            requireContext().contentResolver.openAssetFileDescriptor(imageUri!!, "r")

        fileSize = fileDescriptor!!.length
        if (fileSize!! < 1000000) {
            imgSize = fileSize!! / 1000
            binding.tvSize.text = imgSize.toString().trim() + "KB"
        } else if (fileSize!! > 1000000) {
            imgSize = fileSize!! / 1000000
            binding.tvSize.text = imgSize.toString().trim() + "MB"
        }

        binding.apply {
            imgImage.setImageURI(imageUri)
            tvCodec.text = imageExtension
            imageName.text = requireContext().getFileName(imageUri!!)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCompress.setOnClickListener {
                progressBar.visibility = View.VISIBLE
                compressImage(imageUri!!)
            }

        }
    }

    //get file name
    private fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
        ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
        else -> uri.path?.let(::File)?.name
    }

    private fun Context.getContentFileName(uri: Uri): String? = runCatching {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                .let(cursor::getString)
        }
    }.getOrNull()

    //image compress functions
    private fun compressImage(imageUri: Uri) {
        try {
            val imageBitmap =
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            val path: File =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val fileName = String.format("%d.jpg", System.currentTimeMillis())
            val finalFile = File(path, fileName)
            val fileOutputStream = FileOutputStream(finalFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val imgUri = Uri.fromFile(finalFile)
            val action =
                ImageSelectedFragmentDirections.actionImageSelectedFragmentToResultImageFragment(
                    imgUri, fileSize!!
                )
            findNavController().navigate(action)

        } catch (e: IOException) {
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}


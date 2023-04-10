package com.example.imagecom.ui.video_compress_epic

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.example.imagecom.R
import com.example.imagecom.databinding.FragmentVedioSelectedBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import com.example.imagecom.utils.DialogUtil
import timber.log.Timber
import java.io.File
import java.io.IOException


@Suppress("SameParameterValue")
class VedioSelectedFragment : BaseFragment<FragmentVedioSelectedBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVedioSelectedBinding
        get() = FragmentVedioSelectedBinding::inflate
    private val args by navArgs<VedioSelectedFragmentArgs>()
    private var mediaController: MediaController? = null
    private var videoUri: Uri? = null
    private val cv by lazy { ContentValues() }
    private var videoSize: Long? = null
    private var compressedUri: Uri? = null
    private var progressDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n", "Recycle")
    override fun onCreatedView() {
        videoUri = args.videoUri

        val videoExtension =
            (DocumentFile.fromSingleUri(requireContext(), videoUri!!)?.name ?: "").split('.')[1]

        val fileDescriptor: AssetFileDescriptor? =
            requireContext().contentResolver.openAssetFileDescriptor(videoUri!!, "r")
        val fileSize: Long = fileDescriptor!!.length

        if (fileSize < 1000000) {
            videoSize = fileSize / 1000
            binding.tvSize.text = videoSize.toString().trim() + "KB"
        } else if (fileSize > 1000000) {
            videoSize = fileSize / 1000000
            binding.tvSize.text = videoSize.toString().trim() + "MB"
        }

        binding.apply {
            imgImage.setVideoURI(videoUri)
            tvCodec.text = videoExtension
            val fileName = requireContext().getFileName(videoUri!!)
            imageName.text = fileName
            playVideo()

            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnCompress.setOnClickListener {
                log("Btn compress clicked")
                val date = System.currentTimeMillis()
                compressVideo(videoUri, "compress_$date")
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

    //play video on screen with controller
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

    private fun compressVideo(uri: Uri?, filename: String) {
        log("Compress Video is started")
        if (uri == null) {
            log("uri is null")
            return
        }
        val path = File(
            Environment.getExternalStorageDirectory(),
            Environment.DIRECTORY_MOVIES + "/Compressor"
        )
        try {
            if (!path.exists()) {
                path.mkdir()
            }
        } catch (securityException: Exception) {
            securityException.printStackTrace()
            log("${securityException.message}")
        }

        val date = System.currentTimeMillis()
        cv.apply {
            put(MediaStore.Video.Media.TITLE, "$filename.mp4")
            put(MediaStore.Video.Media.DISPLAY_NAME, "$filename.mp4")
            put(MediaStore.Video.Media.DATA, path.path + "/$filename.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, date)
            put(MediaStore.Video.Media.DATE_MODIFIED, date)
        }
        if (isHigherSdk29()) {
            cv.put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        compressedUri = requireActivity().contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            cv
        )

        getRealPathByUri(requireActivity(), uri)?.let { pathOrigin ->

            VideoCompressor.start(
                pathOrigin,
                "${path.path}/$filename.mp4",
                object : CompressionListener {
                    override fun onProgress(percent: Float) {
                        requireActivity().runOnUiThread {
                            updateDialog(percent)
                        }
                    }

                    override fun onStart() {
                        showDialog()
                    }

                    override fun onSuccess() {
                        onEncodeDone()
                        hideDialog()

                        val action =
                            VedioSelectedFragmentDirections.actionVedioSelectedFragmentToResultVideoFragment(
                                compressedUri!!,
                                videoSize!!
                            )
                        findNavController().navigate(action)
                    }

                    override fun onFailure(failureMessage: String) {
                        onEncodeDone()
                        hideDialog()
                        toast(failureMessage)
                    }

                    override fun onCancelled() {
                        onEncodeDone()
                        hideDialog()
                    }

                }, VideoQuality.MEDIUM, isMinBitRateEnabled = false, keepOriginalResolution = true
            )
        }
    }

    @SuppressLint("LogNotTimber")
    private fun onEncodeDone() {
        if (isHigherSdk29()) {
            cv.apply {
                clear()
                put(MediaStore.Video.Media.SIZE, 0)
                put(MediaStore.Video.Media.IS_PENDING, 0)
            }
            compressedUri?.let { dest ->

                requireActivity().contentResolver.update(dest, cv, null, null)
                val sizeFile =
                    requireActivity().contentResolver.openFileDescriptor(dest, "r")?.use {
                        it.statSize
                    } ?: throw IOException("Could not get file size")
                log("Size compressed video is $sizeFile")
            }
        } else {
            MediaScannerConnection.scanFile(
                requireActivity(),
                arrayOf(Environment.getExternalStorageDirectory().path + "/" + Environment.DIRECTORY_MOVIES + "/Compressor"),
                arrayOf("video/*")
            ) { path, uri ->
                Log.d("OnEncodeDone", "Path: $path and uri: $uri")
            }
        }
    }

    private fun getRealPathByUri(context: Context, uri: Uri): String? {
        var result: String? = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.let { resultCursor ->
                if (resultCursor.moveToFirst()) {
                    val columnId = resultCursor.getColumnIndex(MediaStore.Video.Media.DATA)
                    result = cursor.getString(columnId)
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            log("${exception.message}")
        } finally {
            cursor?.close()
        }
        log("Path : $result")
        return result
    }

    @SuppressLint("SetTextI18n")
    private fun updateDialog(value: Float) {
        val tv = progressDialog?.findViewById<TextView>(R.id.progress_dialog_text)
        tv?.let {
            tv.text = "Progress ${value.toInt()}%"
        }
    }

    private fun showDialog() {
        if (progressDialog == null) {
            initDialog(requireActivity())
        }
        progressDialog?.show()
    }

    private fun hideDialog() {
        progressDialog?.dismiss()
        initDialog(requireActivity())
    }

    private fun initDialog(context: Context) {
        progressDialog = DialogUtil.createProgressDialog(context, "Encoding video")
    }


    private fun isHigherSdk29(): Boolean {
        return isSdkHigherThan(Build.VERSION_CODES.Q)
    }

    private fun isSdkHigherThan(sdk: Int): Boolean {
        return Build.VERSION.SDK_INT >= sdk
    }

    private fun log(msg: String) {
        Timber.tag("IniLogDoang").d("Msg: %s", msg)
    }

    private fun toast(msg: String) {
        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
    }
}




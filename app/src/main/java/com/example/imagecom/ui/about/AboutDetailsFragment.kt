package com.example.imagecom.ui.about

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.imagecom.R
import com.example.imagecom.databinding.FragmentAboutDetailsBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import org.w3c.dom.Document
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class AboutDetailsFragment : BaseFragment<FragmentAboutDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAboutDetailsBinding
        get() = FragmentAboutDetailsBinding::inflate
    override fun onCreatedView() {
        val details: Array<String> = resources.getStringArray(R.array.about)
        val arrayAdapter =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, details)
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            listView.adapter = arrayAdapter
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    when (position) {
                        0 -> {
                            val action =
                                AboutDetailsFragmentDirections.actionAboutDetailsFragmentToContactUsFragment()
                            findNavController().navigate(action)
                        }
                        1 -> {
                            Toast.makeText(requireActivity(),"Thanks",Toast.LENGTH_SHORT).show()
                        }
                        2 -> {
                            readPDF()
                        }
                        3 -> {
                            readPDF()
                        }
                    }
                }
        }
    }
    private fun readPDF() {
        val assetManager: AssetManager = requireActivity().assets
        var `in`: InputStream?
        var out: OutputStream?
        val file = File(requireActivity().filesDir, "Terms_and_Condition.pdf") //<= PDF file Name
        try {
            `in` = assetManager.open("MyPdf.pdf") //<= PDF file Name
            out = requireActivity().openFileOutput(file.name, Context.MODE_WORLD_READABLE)
            copyPdf(`in`, out)
            `in`.close()
            out.flush()
            out.close()
        } catch (e: Exception) {
            println(e.message)
        }
        val packageManager: PackageManager = requireActivity().packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = "application/pdf"
        val list: List<*> =
            packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list.isNotEmpty() && file.isFile) {
            //Toast.makeText(MainActivity.this,"Pdf Reader Exist !",Toast.LENGTH_SHORT).show();
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(
                Uri.parse("file://" + requireActivity().filesDir + "/terms_and_condition.pdf"),
                "application/pdf"
            )
            startActivity(intent)
        } else {
            // show toast when => The PDF Reader is not installed !
            Toast.makeText(requireActivity(), "Pdf Reader NOT Exist !", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun copyPdf(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }
}
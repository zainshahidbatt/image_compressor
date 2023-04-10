package com.example.imagecom.ui.about

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.findNavController
import com.example.imagecom.databinding.FragmentContactUsBinding
import com.example.imagecom.ui.basefragment.BaseFragment
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ContactUsFragment : BaseFragment<FragmentContactUsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentContactUsBinding
        get() = FragmentContactUsBinding::inflate

    override fun onCreatedView() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnFacebook.setOnClickListener {
                openBrowser("https://www.facebook.com/zainshahidbatt/")
            }
            btnEmail.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("zainshahidbatt@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Compressor Mobile Ap")
                intent.type = "text/html"
                intent.setPackage("com.google.android.gm")
                startActivity(Intent.createChooser(intent, "Send mail"))
            }
        }
    }

    private fun openBrowser(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(url))
    }

}
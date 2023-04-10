package com.example.imagecom.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.example.imagecom.R

object DialogUtil {

    fun createProgressDialog(context: Context, text: String ) : AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        inflater?.let { inflaterNonNull ->
            val dialogView = inflaterNonNull.inflate(R.layout.progress_dialog_layout, null)
            builder.setView(dialogView)
            val textView = dialogView.findViewById<TextView>(R.id.progress_dialog_text)
            if (textView != null) {
                textView.text = text
            }
        }
        builder.setCancelable(false)
        return builder.create()
    }

}
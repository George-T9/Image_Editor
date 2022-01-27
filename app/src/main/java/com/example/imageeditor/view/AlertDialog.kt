package com.example.imageeditor.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.imageeditor.R
import java.lang.IllegalStateException

class AlertDialogLoading : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            builder.setCancelable(false)
            builder.setView(R.layout.progress_indicator)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}


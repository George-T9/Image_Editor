package com.example.imageeditor.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.imageeditor.R
import java.lang.ClassCastException

class ImageSaveDialogFragment(private var listener : SaveDialogListener) : DialogFragment() {


    interface SaveDialogListener {
        fun onPositiveClick(dialog: DialogFragment)
        fun onCancelClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Save")
            builder.setMessage("Do you want to save Image?")
                .setPositiveButton("Save") { dialog, id ->
                    listener.onPositiveClick(this)
                }
                .setNeutralButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
                .setNegativeButton("Don't save") { dialog, id ->
                    listener.onCancelClick(this)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SaveDialogListener
        } catch (e: ClassCastException) {

        }
    }
}
package com.example.imageeditor.view.editor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException

fun saveImage(context: Context, bitmap: Bitmap) {
    val bytes = ByteArrayOutputStream()
    try {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "Edited_image",
            null
        )
        Log.d("TAG", path)
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
    }
}

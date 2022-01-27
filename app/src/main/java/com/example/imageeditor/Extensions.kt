package com.example.imageeditor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun Bitmap.convertToUri(context: Context): Uri {

    var tempDir: File = context.externalCacheDir!!

    tempDir = File(tempDir.absolutePath + "/temp/")
    tempDir.mkdir()

    val tempFile: File = File.createTempFile("tempFile", ".jpg", tempDir)
    val bytes = ByteArrayOutputStream()

    this.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val bitmapData = bytes.toByteArray()

    //write the bytes in file
    val fos = FileOutputStream(tempFile)
    fos.write(bitmapData)
    fos.flush()
    fos.close()
    return Uri.fromFile(tempFile)

}

//fun Bitmap.rotatedBy(angle: Float): Bitmap? {
//    val matrix = Matrix().apply { postRotate(angle) }
//    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
//}

fun Uri.convertToBitmap(context: Context): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, this)
        ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)

    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, this)
    }


}
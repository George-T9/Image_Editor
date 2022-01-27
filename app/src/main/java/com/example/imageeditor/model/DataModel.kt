package com.example.imageeditor.model

import android.graphics.Bitmap
import android.net.Uri
import com.example.imageeditor.EditorTools
import com.example.imageeditor.Filters

data class EditedImage(
    val uri:Uri,
    val editorTool: EditorTools
)

data class FilterList(
    val image: Int,
    val title: Filters
)
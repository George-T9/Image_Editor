package com.example.imageeditor

import android.graphics.BitmapFactory
import com.example.imageeditor.model.EditedImage
import com.example.imageeditor.model.FilterList


object Repository {

    val imageList = ArrayList<EditedImage>()

    val filterList: List<FilterList> = listOf(

        FilterList(
            R.drawable.image_tint,
            Filters.Tint
        ),
        FilterList(
            R.drawable.image_gaussion,
            Filters.Gaussion
        ),
        FilterList(
            R.drawable.image_sepia,
            Filters.Sepia
        ),
        FilterList(
            R.drawable.image_snow,
            Filters.Snow
        ),
        FilterList(
            R.drawable.image_grayscale,
            Filters.GrayScale
        ),
        FilterList(
            R.drawable.image_contrast,
            Filters.Contrast
        ),
        FilterList(
            R.drawable.image_engrave,
            Filters.Engrave
        ),
        FilterList(
            R.drawable.image_grayscale,
            Filters.GrayScale
        ),
        FilterList(
            R.drawable.image_flea,
            Filters.Flea
        )
    )
}
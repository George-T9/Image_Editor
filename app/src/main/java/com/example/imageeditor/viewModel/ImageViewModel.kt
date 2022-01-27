package com.example.imageeditor.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imageeditor.model.EditedImage
import com.example.imageeditor.EditorTools
import com.example.imageeditor.Repository


class ImageViewModel : ViewModel() {

    var isSaved =false
    var firstImage = MutableLiveData<Uri>()
    var imageListLiveData = MutableLiveData<List<EditedImage>>()


    fun add(editingTool: EditedImage) {
            if (editingTool.editorTool == EditorTools.Initial) {
                if (Repository.imageList.isNotEmpty()) {
                    Repository.imageList.clear()
                }
                Repository.imageList.add(editingTool)
                imageListLiveData.value = Repository.imageList
            } else {
                Repository.imageList.add(editingTool)
                imageListLiveData.value = Repository.imageList
            }
        isSaved = false
    }

    fun removeLast() {
        if (Repository.imageList.size > 1) {
            Repository.imageList.removeLast()
            imageListLiveData.value = Repository.imageList
        }
    }

    fun clear() {
        val firstPic = Repository.imageList.first()
        Repository.imageList.clear()
        Repository.imageList.add(firstPic)
        imageListLiveData.value = Repository.imageList
    }

    fun getLast(): EditedImage {
        return Repository.imageList.last()
    }

    fun replace(editingTool: EditedImage) {
        Repository.imageList.removeLast()
        Repository.imageList.add(editingTool)
        imageListLiveData.value = Repository.imageList
        isSaved = false
    }

    fun save(){
        val last = Repository.imageList.last()
        Repository.imageList.clear()
        Repository.imageList.add(last)
        imageListLiveData.value=Repository.imageList
        isSaved = true
    }

}

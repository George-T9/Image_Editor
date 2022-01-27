package com.example.imageeditor.view.editor

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.imageeditor.*
import com.example.imageeditor.databinding.FragmentEditBinding
import com.example.imageeditor.model.EditedImage
import com.example.imageeditor.model.FilterList
import com.example.imageeditor.view.AlertDialogLoading
import com.example.imageeditor.view.ImageSaveDialogFragment
import com.example.imageeditor.viewModel.ImageViewModel
import com.google.android.material.snackbar.Snackbar
import com.mukesh.image_processing.ImageProcessor
import kotlinx.coroutines.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class EditorFragment : Fragment(), FilterListAdapter.OnClickListener,
    ImageSaveDialogFragment.SaveDialogListener {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var model: ImageViewModel

    private val filterList = ArrayList<FilterList>()

    private lateinit var processor: ImageProcessor

    private val TAG = "Editor Activity"

    var filterIsShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.editor_menu)
        model = ViewModelProvider(requireActivity()).get(ImageViewModel::class.java)
        setupObserver()
        setupUI()
    }

    private fun setupUI() {

        binding.rvFilter.visibility = View.INVISIBLE
        processor = ImageProcessor()

        Glide.with(this).load(model.getLast().uri).into(binding.imageViewEditor)

        binding.toolbar.setNavigationOnClickListener {
            if(model.isSaved== false){
                val dialog = ImageSaveDialogFragment(this@EditorFragment)
                dialog.show(requireActivity().supportFragmentManager, "Image Save Dialog")

            }else{
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).popBackStack()
            }
          }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.nav_save -> {
                    model.save()
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack()
                    true
                }
                else -> {
                    false
                }
            }
        }
        binding.btnCrop.setOnClickListener {
            cropImage.launch(
                options(uri = model.getLast().uri as Uri) {
                    setGuidelines(CropImageView.Guidelines.ON)
                    setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                    setAllowFlipping(false)
                    setAllowRotation(false)
                }
            )
        }

        binding.btnUndo.setOnClickListener {
            model.removeLast()
        }

        binding.btnRotateLeft.setOnClickListener {
            val item: EditedImage = model.getLast()
            val rotatedImage = processor.rotate(item.uri.convertToBitmap(requireActivity()), -90F)
            if (item.editorTool == EditorTools.Rotate) {
                model.replace(
                    EditedImage(
                        rotatedImage.convertToUri(requireActivity()),
                        EditorTools.Rotate
                    )
                )
            } else {
                model.add(
                    EditedImage(
                        rotatedImage.convertToUri(requireActivity()),
                        EditorTools.Rotate
                    )
                )
            }
        }

        binding.btnRotateRight.setOnClickListener {
            val item: EditedImage = model.getLast()
            val rotatedImage = processor.rotate(item.uri.convertToBitmap(requireActivity()), 90F)
            if (item.editorTool == EditorTools.Rotate) {
                model.replace(
                    EditedImage(
                        rotatedImage.convertToUri(requireActivity()),
                        EditorTools.Rotate
                    )
                )
            } else {
                model.add(
                    EditedImage(
                        rotatedImage.convertToUri(requireActivity()),
                        EditorTools.Rotate
                    )
                )
            }
        }

        binding.btnFilter.setOnClickListener {
            if (filterIsShown) {
                slideDown(binding.rvFilter)
            } else {
                slideUp(binding.rvFilter)
            }
        }

        binding.rvFilter.apply {
            adapter = FilterListAdapter(requireActivity(), Repository.filterList, this@EditorFragment)
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun setupObserver() {
        model.imageListLiveData.observe(viewLifecycleOwner, {
            Glide.with(this).load(it.last().uri).into(binding.imageViewEditor)
        })
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        try {
            if (result.isSuccessful) {
                val uri = result.uriContent!!
                model.add(EditedImage(uri, EditorTools.Crop))
            } else {
                val throwable = result.error
                Log.d(TAG, throwable.toString())
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onDestroy() {
        model.clear()
        super.onDestroy()
    }

    private fun slideUp(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F, 0F, view.height.toFloat(), 0F
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
        filterIsShown = true
    }

    private fun slideDown(view: View) {
        view.visibility = View.VISIBLE
        val animate = TranslateAnimation(
            0F, 0F, 0F, view.height.toFloat()
        )
        animate.duration = 500
        animate.fillAfter = true
        view.startAnimation(animate)
        filterIsShown = false
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onItemClick(item: FilterList) {
        val bitmap: Bitmap = model.getLast().uri.convertToBitmap(requireActivity())
        var filteredImage: Bitmap?
        val dialog = AlertDialogLoading()
        slideDown(binding.rvFilter)
        dialog.show(requireActivity().supportFragmentManager, "Loading")
        when (item.title) {
            Filters.Tint -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.tintImage(bitmap, 90)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Gaussion -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.applyGaussianBlur(bitmap)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.GrayScale -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.doGreyScale(bitmap)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Sepia -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.createSepiaToningEffect(bitmap, 1, 2.0, 1.0, 5.0)

                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Snow -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.applySnowEffect(bitmap)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Saturation -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.applySaturationFilter(bitmap, 3)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Engrave -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.engrave(bitmap)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Contrast -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.createContrast(bitmap, 1.5)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()
                    }
                }
            }
            Filters.Flea -> {
                GlobalScope.launch(Dispatchers.IO) {
                    filteredImage = processor.applyFleaEffect(bitmap)
                    withContext(Dispatchers.Main) {
                        model.add(
                            EditedImage(
                                filteredImage!!.convertToUri(requireActivity()),
                                EditorTools.Filter
                            )
                        )
                        dialog.dismiss()

                    }
                }
            }
        }
    }

    override fun onPositiveClick(dialog: DialogFragment) {
        val bitmap = model.getLast().uri.convertToBitmap(requireActivity())
        saveImage(requireActivity(), bitmap)
        model.save()
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack()
    }

    override fun onCancelClick(dialog: DialogFragment) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack()
    }
}
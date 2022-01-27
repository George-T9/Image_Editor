package com.example.imageeditor.view.home

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.imageeditor.EditorTools
import com.example.imageeditor.PermissionRequest
import com.example.imageeditor.R
import com.example.imageeditor.databinding.FragmentHomeBinding
import com.example.imageeditor.model.EditedImage
import com.example.imageeditor.view.AlertDialogLoading
import com.example.imageeditor.viewModel.ImageViewModel

class HomeFragment : Fragment() {

    //initializing ViewBinding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //initializing ViewModel
    private lateinit var model: ImageViewModel

    private lateinit var loadingDialog: DialogFragment
    private var _currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(ImageViewModel::class.java)

        loadingDialog = AlertDialogLoading()
        setUpUI()
        setUpObserver()
    }


    private fun setUpObserver() {
        model.imageListLiveData.observe(viewLifecycleOwner, {
            Glide.with(requireActivity()).load(it.first().uri).into(binding.imageView)
        })
    }

    private fun setUpUI() {
        binding.btnUpload.setOnClickListener {
            onUpload()
        }

        binding.btnSelfie.setOnClickListener {
            onCapture()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // on camera open
    private fun onCapture() {
        if (PermissionRequest(requireActivity())) {
            val value = ContentValues()
            value.put(MediaStore.Images.Media.TITLE, "New_image")
            value.put(MediaStore.Images.Media.DESCRIPTION, "New Image")

            _currentImageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                value
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, _currentImageUri)
            cameraResultLauncher.launch(intent)
        }
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                _currentImageUri?.let {
                    model.add(EditedImage(it, EditorTools.Initial))
                }
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_homeFragment_to_editorFragment2)
            }
        }

    //on gallery open
    private fun onUpload() {
        if (PermissionRequest(requireActivity())) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            uploadResultLauncher.launch(intent)
        }
    }

    private val uploadResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    val selectedImageUri: Uri? = result.data!!.data
                    selectedImageUri?.let {
                        model.add(EditedImage(it, EditorTools.Initial))
                    }
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_homeFragment_to_editorFragment2)
                }
            }
        }

}
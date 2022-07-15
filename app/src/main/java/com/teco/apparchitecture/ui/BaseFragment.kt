package com.teco.apparchitecture.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.teco.apparchitecture.R
import com.teco.apparchitecture.listener.AppLifeCycle
import com.teco.apparchitecture.util.AppPref
import com.teco.apparchitecture.util.AppUtil.showSnackMessage
import com.yalantis.ucrop.UCrop
import java.io.File

abstract class BaseFragment<VB : ViewBinding> : Fragment(), AppLifeCycle {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    open var imageDir: File? = null
    var captureImageUri: Uri? = null
    private var captureImageFile: File? = null
    open var cropImageFile: File? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(layoutInflater, container, false)
        (activity as MainActivity).setAppLifeCycleLister(this)
        return requireNotNull(_binding).root
    }

    abstract fun viewCreated(view: View)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated(view)
    }

    private fun createImageDireIfNotExist() {
        imageDir = File(
            requireActivity().externalCacheDir,
            "Image"
        )
        if (!imageDir!!.exists()) imageDir!!.mkdir()
    }

    open fun showImageSelectionDialog(){
        AlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setTitle("Choose image source")
            .setMessage("You can select image form gallery or can capture from camera")
            .setPositiveButton("Gallery"){ dialog, _ ->
                dialog.dismiss()
                selectImageFromGallery()
            }.setNegativeButton("Camera"){ dialog, _ ->
                dialog.dismiss()
                selectImageFromCamera()
            }.setNeutralButton("Cancel"){ dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    open fun selectImageFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    1234
                )
                return
            }
        }

        createImageDireIfNotExist()
        captureImageFile = File(imageDir, System.currentTimeMillis().toString() + ".png")
        if (!captureImageFile!!.exists()) captureImageFile!!.createNewFile()
        captureImageUri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName,
            captureImageFile!!
        )
        AppPref.storeCaptureImagePath(
            requireContext(),
            captureImageUri.toString()
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(captureImageUri)
    }

    open fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        selectImageFromGallery.launch(
            Intent.createChooser(intent, "Select Picture")
        )
    }

    private var selectImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                createImageDireIfNotExist()
                cropImageFile = File(imageDir, System.currentTimeMillis().toString() + ".png")
                if (!cropImageFile!!.exists())
                    cropImageFile!!.createNewFile()
                cropImageLauncher.launch(
                    UCrop.of(result.data!!.data!!, cropImageFile!!.toUri())
                        .withAspectRatio(16f, 16f)
                        .getIntent(requireContext())
                )
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                createImageDireIfNotExist()
                cropImageFile = File(imageDir, System.currentTimeMillis().toString() + ".png")
                Log.e("ImageDir", "$cropImageFile")
                if (!cropImageFile!!.exists()) cropImageFile!!.createNewFile()
                cropImageLauncher.launch(
                    UCrop.of(
                        AppPref.getCaptureImageUri(requireContext())!!,
                        cropImageFile!!.toUri()
                    )
                        .withAspectRatio(16f, 16f)
                        .getIntent(requireContext())
                )
            }
        }

    private var cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageSelectedSuccess()
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                showSnackMessage(
                    requireContext(),
                    binding.root,
                    "Image is not supported"
                )
            }
        }


    open fun imageSelectedSuccess(){}

    fun showSnackMessage(message: String){
        showSnackMessage(
            requireContext(),
            binding.root,
            message
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun navigateToFragment(action: Int, bundle: Bundle? = null) {
        findNavController().navigate(action, bundle)
    }

    protected fun onBackPressed() {
        findNavController().popBackStack()
    }

    companion object {
        const val TAG = "BaseFragment"
    }

}
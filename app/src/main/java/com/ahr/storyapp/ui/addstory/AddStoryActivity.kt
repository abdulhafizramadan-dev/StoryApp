package com.ahr.storyapp.ui.addstory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.ActivityAddStoryBinding
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.ui.MainActivity
import com.ahr.storyapp.ui.camera.CameraActivity
import com.ahr.storyapp.ui.camera.CameraActivity.Companion.CAMERA_X_RESULT
import com.ahr.storyapp.helper.reduceFileImage
import com.ahr.storyapp.helper.rotateBitmap
import com.ahr.storyapp.helper.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.Executors

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.M)
class AddStoryActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        enum class PhotoSource {
            CAMERA, GALLERY
        }
        const val EXTRA_TOKEN = "extra_token"
    }

    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    private var selectedPhotoSource: PhotoSource = PhotoSource.CAMERA

    private var storyImage: File? = null
    private var isBackCamera = true

    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupButtonsPhoto()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_camera -> selectedPhotoSource = PhotoSource.CAMERA
            R.id.btn_gallery -> selectedPhotoSource = PhotoSource.GALLERY
            R.id.btn_photo -> startPhoto()
            R.id.cb_user_location -> toggleLocation()
            R.id.btn_add_story -> uploadNewStory()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupButtonsPhoto() {
        arrayOf(
            binding.btnCamera,
            binding.btnGallery,
            binding.btnPhoto,
            binding.cbUserLocation,
            binding.btnAddStory
        ).forEach { button -> button.setOnClickListener(this) }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun toggleLoading(state: Boolean) {
        val progressVisibility = if (state) View.VISIBLE else View.GONE
        binding.progressBar.visibility = progressVisibility
        binding.btnAddStory.isEnabled = !state
    }

    private fun startPhoto() {
        when (selectedPhotoSource) {
            PhotoSource.CAMERA -> startCamera()
            PhotoSource.GALLERY -> startGallery()
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getCurrentLocation()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getCurrentLocation()
            else -> binding.cbUserLocation.isChecked = false
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val imageFile = uriToFile(selectedImage, this)
            storyImage = imageFile
            binding.ivStory.setImageURI(selectedImage)
        }
    }

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode != CAMERA_X_RESULT) return@registerForActivityResult
        executor.execute {
            val selectedImage = it.data?.getSerializableExtra("picture") as File
            storyImage = selectedImage
            isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(selectedImage.path),
                isBackCamera
            )
            handler.post { binding.ivStory.setImageBitmap(result) }
        }
    }

    private fun uploadNewStory() {
        if (validateFormLogin()) return

        toggleLoading(true)

        if (!binding.cbUserLocation.isChecked) currentLocation = null

        executor.execute {
            val token = intent?.getStringExtra(EXTRA_TOKEN)
            val rotateBitmap = selectedPhotoSource == PhotoSource.CAMERA
            val storyImage = reduceFileImage(storyImage as File, rotateBitmap, isBackCamera)
            val description = binding.edtDescription.text.toString()

            handler.post {
                addStoryViewModel.uploadNewStory(
                    "Bearer $token",
                    storyImage,
                    description,
                    currentLocation?.latitude,
                    currentLocation?.longitude
                ).observe(this) { response ->
                    when (response) {
                        is Response.Loading -> toggleLoading(true)
                        is Response.Empty -> toggleLoading(false)
                        is Response.Success -> {
                            toggleLoading(false)
                            Snackbar.make(binding.root, response.data, Snackbar.LENGTH_LONG).show()
                            navigateToHomeScreen()
                        }
                        is Response.Error -> {
                            toggleLoading(false)
                            response.message?.let {
                                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentLocation() {
        val isPermissionGranted =
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (isPermissionGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun toggleLocation() {
        if (binding.cbUserLocation.isChecked) {
            getCurrentLocation()
        } else {
            currentLocation = null
        }
    }

    private fun validateFormLogin(): Boolean {
        if (storyImage == null) {
            Snackbar.make(binding.root, getString(R.string.image_empty), Snackbar.LENGTH_LONG)
                .show()
            return true
        }

        if (binding.edtDescription.text?.isEmpty() == true) {
            Snackbar.make(
                binding.root,
                getString(R.string.validation_error_description),
                Snackbar.LENGTH_LONG
            ).show()
            return true
        }

        return false
    }
}
package com.ahr.storyapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.ActivityMapsBinding
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.Story
import com.ahr.storyapp.ui.auth.AuthViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val authViewModel: AuthViewModel by viewModels()
    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFullScreen()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        authViewModel.getAuthToken()

        setupMapStyle()
        setupListener()
        observeAuth()
        getCurrentLocation()
    }

    private fun setupFullScreen() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupMapStyle() {
        try {
            val isLoadMapStyleSuccess =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!isLoadMapStyleSuccess) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.failed_load_maps_styles),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this, getString(R.string.maps_styles_not_found), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addStoryMarks(stories: List<Story>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(getString(
                            R.string.snippet_map_format,
                            latLng.latitude.toString(),
                            latLng.longitude.toString()
                        )
                    )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getCurrentLocation()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getCurrentLocation()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCurrentLocation() {
        val isPermissionGranted =
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (isPermissionGranted) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                val currentLocation = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 8f))
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

    private fun observeAuth() {
        authViewModel.authToken.observe(this) { authToken ->
            if (authToken.isNotEmpty()) {
                mapsViewModel.getStories("Bearer $authToken").observe(this) { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Empty -> Snackbar.make(
                            binding.root,
                            getString(R.string.empty_story),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                        is Response.Success -> addStoryMarks(response.data)
                        is Response.Error -> response.message?.let {
                            Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).show()
                        }
                    }
                }
            }
        }
    }
}
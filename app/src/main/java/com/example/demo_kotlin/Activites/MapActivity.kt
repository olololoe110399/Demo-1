package com.example.demo_kotlin.Activites

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.demo_kotlin.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback, ConnectionCallbacks,
    OnConnectionFailedListener, LocationListener {
    private var mMap: GoogleMap? = null
    private var myProgress: ProgressDialog? = null
    private var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        //Toolbar
        toolbar = findViewById(R.id.toolbarmap)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Map"
        //Anh xa Searchview
// Tạo Progress Bar
        myProgress = ProgressDialog(this)
        myProgress!!.setTitle("Map Loading ...")
        myProgress!!.setMessage("Please wait...")
        myProgress!!.setCancelable(true)
        // Hiển thị Progress Bar
        myProgress!!.show()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mnmap, menu)
        val menuItem = menu.findItem(R.id.search_map)
        val searchView =
            menuItem.actionView as SearchView
        val intent = intent
        searchView.setQuery(intent.getStringExtra("address"), false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null
                if (location != null || location != "") {
                    val geocoder = Geocoder(this@MapActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0]
                        val latLng = LatLng(address.latitude, address.longitude)
                        mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                    } else {
                        Toast.makeText(this@MapActivity, "No found location!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Thiết lập sự kiện đã tải Map thành công
        mMap!!.setOnMapLoadedCallback {
            // Đã tải thành công thì tắt Dialog Progress đi
            myProgress!!.dismiss()
            // Hiển thị vị trí người dùng.
            Checkpermission()
        }
        val latLng = LatLng(16.075749, 108.169950)
        mMap!!.addMarker(MarkerOptions().position(latLng).title("FPT POLYTECHNIC"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
    }

    private fun Checkpermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap!!.uiSettings.isZoomControlsEnabled = true
            mMap!!.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Error Permission Map", Toast.LENGTH_LONG).show()
            askPermissions()
        }
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            111
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                // Chú ý: Nếu yêu cầu bị bỏ qua, mảng kết quả là rỗng.
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show()
                    mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                    mMap!!.uiSettings.isZoomControlsEnabled = true
                    mMap!!.isMyLocationEnabled = true
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }// Tiêu chí để tìm một nhà cung cấp vị trí.
    // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
// ==> "gps", "network",...

    // Tìm một nhà cung cấp vị trị hiện thời đang được mở.
    private val enabledLocationProvider: String?
        private get() {
            val locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // Tiêu chí để tìm một nhà cung cấp vị trí.
            val criteria = Criteria()
            // Tìm một nhà cung vị trí hiện thời tốt nhất theo tiêu chí trên.
// ==> "gps", "network",...
            val bestProvider = locationManager.getBestProvider(criteria, true)
            val enabled = locationManager.isProviderEnabled(bestProvider)
            if (!enabled) {
                Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show()
                Log.i("DuyMap", "No location provider enabled!")
                return null
            }
            return bestProvider
        }

    override fun onLocationChanged(location: Location) {}
    override fun onConnected(bundle: Bundle?) {}
    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
}
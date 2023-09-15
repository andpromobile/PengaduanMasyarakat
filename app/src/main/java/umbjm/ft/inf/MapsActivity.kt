package umbjm.ft.inf

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import umbjm.ft.inf.databinding.ActivityMapsBinding
import java.util.Locale

class MapsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker: Marker
    private lateinit var geocoder: Geocoder
//    private var lat:Double = -2.585882680757
//    private var lng:Double = 115.38438320159
    private var lat:Double = 0.0
    private var lng:Double = 0.0
    private var addressLine:String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(binding.root)

        inisialisasi()

        binding.pilihLokasi.setOnClickListener{
            intent = Intent()
            intent.putExtra("lokasi", this.addressLine)
            intent.putExtra("latitude", this.lat.toString())
            intent.putExtra("longitude", this.lng.toString())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun inisialisasi(){

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                    CancellationTokenSource().token

                override fun isCancellationRequested() = false
            })
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Toast.makeText(
                        this,
                        "Mohon Aktifkan GPS / Fitur Lokasi Terlebih Dahulu.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                else {
                    lat = location.latitude
                    lng = location.longitude

                    geocoder = Geocoder(this, Locale.getDefault())
                    val addressList = geocoder.getFromLocation(lat, lng, 1)
                    addressLine = addressList?.get(0)?.getAddressLine(0)

                    val startPoint = GeoPoint(lat, lng)
                    binding.map.setMultiTouchControls(true)
                    binding.map.controller.animateTo(startPoint)
                    binding.map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                    binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                    val mapController = binding.map.controller
                    mapController.setCenter(startPoint)
                    mapController.setZoom(15)

                    marker = Marker(binding.map)
                    marker.icon = resources.getDrawable(R.drawable.ic_place)
                    marker.snippet = addressLine
                    marker.position = GeoPoint(startPoint)

                    binding.tvAddress.text = addressLine
                    binding.lat.text = "Latitude: $lat"
                    binding.lng.text = "Longitude: $lng"

                    binding.map.overlays.add(marker)
                    binding.map.invalidate()

                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {

                            val addressList = geocoder.getFromLocation(p!!.latitude, p.longitude, 1)
                            addressLine = addressList?.get(0)?.getAddressLine(0)

                            marker.snippet = addressLine
                            marker.position = GeoPoint(p)

                            lat = p.latitude
                            lng = p.longitude

                            binding.tvAddress.text = addressLine
                            binding.lat.text = "Latitude: $lat"
                            binding.lng.text = "Longitude: $lng"

                            binding.tvAddress.text = addressLine
                            binding.map.invalidate()

                            return false
                        }

                        override fun longPressHelper(p: GeoPoint?): Boolean {
                            return false
                        }
                    }

                    val eventsOverlay = MapEventsOverlay(applicationContext, mapEventsReceiver)
                    binding.map.overlays.add(eventsOverlay)
                }
            }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding.map.onResume()
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        binding.map.onPause()
    }
}
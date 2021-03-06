package br.usjt.ucsist.chatfirebase.ui

import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.usjt.ucsist.chatfirebase.R
import br.usjt.ucsist.chatfirebase.model.Mensagem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.seismic.ShakeDetector
import com.squareup.seismic.ShakeDetector.SENSITIVITY_HARD
import com.squareup.seismic.ShakeDetector.SENSITIVITY_LIGHT


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, ShakeDetector.Listener {

    private lateinit var mMap: GoogleMap
    private var mensagem: Mensagem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val intent = intent
        mensagem = intent.getSerializableExtra("mensagem") as Mensagem
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sd = ShakeDetector(this)
        sd.setSensitivity(SENSITIVITY_LIGHT)
        sd.start(sensorManager)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if(mensagem != null){
            val userLocation = LatLng(mensagem!!.latitude!!, mensagem!!.longitude!!)
            mMap.addMarker(
                MarkerOptions().position(userLocation).title("Localização de " + mensagem!!.usuario)
            )
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    userLocation, 16.0f
                )
            )

        }
    }

    override fun hearShake() {
        finish()
    }


}
package com.example.taller2.Logica

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2.Datos.Datos
import com.example.taller2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.taller2.databinding.ActivityMapaConGoogleBinding
import com.google.android.gms.maps.model.MapStyleOptions
import java.io.IOException

class MapaConGoogle : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapaConGoogleBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightSensorListener: SensorEventListener
    private lateinit var  direccion : EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BODY_SENSORS)

        pedirPermiso(this, permisos,"Se necesitan estos permisos para todas las funcionalidades de la app",
            Datos.MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION)

        binding = ActivityMapaConGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        direccion = binding.direccion

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (mMap != null) {
                    val context = this@MapaConGoogle // Obtener el contexto de la actividad
                    val luxValue = event.values[0]

                    // Ajustar el umbral según tus necesidades
                    val threshold = 10000

                    if (luxValue < threshold) {
                        Log.i("MAPS", "DARK MAP: $luxValue")
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_noche))
                    } else {
                        Log.i("MAPS", "LIGHT MAP: $luxValue")
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.style_retro))
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }


    }



    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(lightSensorListener, lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorListener)
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
    val sydney = LatLng(4.628754841501681, -74.06465377597839)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        // Add a marker in Sydney and move the camera
        //4.628754841501681, -74.06465377597839
        mMap.addMarker(MarkerOptions().position(sydney).title("Pontifica Universidad Javeriana"))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_retro))

        direccion.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val address = direccion.text.toString()
                colocarMarcador(address)
                return@setOnEditorActionListener true
            }
            false
        }

        mMap.setOnMapClickListener { latLng ->
            val geocoder = Geocoder(this@MapaConGoogle)
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1)?.firstOrNull()
                ?.getAddressLine(0) ?: "Dirección desconocida"
            mMap.addMarker(MarkerOptions().position(latLng).title(address))
            var distancia = 0.0
            distancia = calcularDistancia(latLng,sydney)
            Toast.makeText(this, "la distancia entre el punto de partida y este es: ${distancia}km",
                Toast.LENGTH_SHORT).show()

        }

    }

    fun pedirPermiso(context: Activity, permisos: Array<String>, justificacion: String, idCode: Int) {


        if(ContextCompat.checkSelfPermission(context,permisos[0]) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,permisos[1]) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permisos[0])) {
                Toast.makeText(this, "se necesita permiso para esta funcion", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(context, arrayOf(permisos[0]), idCode)
            }
            if(ActivityCompat.shouldShowRequestPermissionRationale(context,permisos[1])){
                Toast.makeText(this, "se necesita permiso para esta funcion", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(context, arrayOf(permisos[1]), idCode)
            }


            ActivityCompat.requestPermissions(context, permisos, idCode)
        }
        else{

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Datos.MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                    Toast.makeText(this, "!Gracias", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(this, "Funcionalidades Limitadas", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun colocarMarcador(direccion: String) {
        val geocoder = Geocoder(this@MapaConGoogle)

        try {
            val addresses = geocoder.getFromLocationName(direccion, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val location = addresses[0]
                    val latLng = LatLng(location.latitude, location.longitude)
                    var distancia = 0.0

                    mMap.addMarker(MarkerOptions().position(latLng).title(direccion))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    distancia = calcularDistancia(latLng,sydney)
                    Toast.makeText(this, "la distancia entre el punto de partida y este es: ${distancia}km",
                        Toast.LENGTH_SHORT).show()

                } else {
                    // No se encontró la dirección
                    // Puedes mostrar un mensaje al usuario aquí
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Manejar errores de geocodificación
        }
    }

    private fun calcularDistancia(latLng1: LatLng, latLng2: LatLng): Double {
        val earthRadius = 6371 // Radio de la Tierra en kilómetros

        val dLat = Math.toRadians(latLng2.latitude - latLng1.latitude)
        val dLon = Math.toRadians(latLng2.longitude - latLng1.longitude)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latLng1.latitude)) * Math.cos(Math.toRadians(latLng2.latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        val distance = earthRadius * c

        return distance

    }
}
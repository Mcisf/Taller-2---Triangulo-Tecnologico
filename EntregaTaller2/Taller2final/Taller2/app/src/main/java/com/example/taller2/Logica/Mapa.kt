package com.example.taller2.Logica

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2.Datos.Datos
import com.example.taller2.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

import org.osmdroid.views.overlay.Marker

class mapa : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var map: MapView
    private lateinit var tilesOverlay: TilesOverlay
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager





    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLocation = GeoPoint(location.latitude, location.longitude)
            val marker = Marker(map)
            marker.position = currentLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
            map.controller.setCenter(currentLocation)
        }

    }

    private val lightSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT)
            {
                val lux = event.values[0]
                // Ajusta el umbral de luminosidad según tus preferencias
                if (lux < 50) {
                    // Modo oscuro
                    tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)// Ejemplo con un estilo oscuro
                } else {
                    // Modo claro
                    tilesOverlay.setColorFilter(null) // Ejemplo con un estilo claro
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No es necesario implementar este método para este ejemplo
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mapa)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val permisos = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        //pide el permiso de ubicación
        pedirPermiso(
            this, permisos, "Se necesitan estos permisos para todas las funcionalidades de la app",
            Datos.MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION
        )

        //sensor de luz

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // inicialización del mapa

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )


        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(20.0)
        tilesOverlay = map.overlayManager.tilesOverlay

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Asumimos que el permiso ya ha sido otorgado
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )


    }


    fun pedirPermiso(
        context: Activity,
        permisos: Array<String>,
        justificacion: String,
        idCode: Int
    ) {

        if (ContextCompat.checkSelfPermission(
                context,
                permisos[0]
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                context,
                permisos[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permisos[0])) {
                Toast.makeText(context, "se necesita permiso para esta funcion", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.requestPermissions(context, arrayOf(permisos[0]), idCode)
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permisos[1])) {
                Toast.makeText(context, "se necesita permiso para esta funcion", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.requestPermissions(context, arrayOf(permisos[1]), idCode)
            }

            ActivityCompat.requestPermissions(context, permisos, idCode)
        } else {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Datos.MY_PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED )) {
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
}
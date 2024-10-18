package com.example.taller2.Logica

import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2.R

class Camara : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val permisoCamara = 1
    private val permisoGaleria = 2
    private val permisoCodigo = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)

        imageView = findViewById(R.id.imageView)
        val botonCamara = findViewById<Button>(R.id.botonCamara)
        val botonGaleria = findViewById<Button>(R.id.botonGaleria)

        botonCamara.setOnClickListener {
            if (revisarPermisos()) {
                camara()
            } else {
                pedirPermisos()
            }
        }

        botonGaleria.setOnClickListener {
            if (revisarPermisos()) {
                galeria()
            } else {
                pedirPermisos()
            }
        }
    }

    // Método onResume que se ejecuta cada vez que la actividad vuelve a estar visible
    override fun onResume() {
        super.onResume()
        // Verificar los permisos cuando la actividad vuelve a primer plano
        if (!revisarPermisos()) {
            pedirPermisos()
        }
    }

    private fun revisarPermisos(): Boolean {
        val permisoCamara = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val lecturaAlmacen = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val escrituraAlmacen = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return permisoCamara == PackageManager.PERMISSION_GRANTED &&
                lecturaAlmacen == PackageManager.PERMISSION_GRANTED &&
                escrituraAlmacen == PackageManager.PERMISSION_GRANTED
    }

    private fun pedirPermisos() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            permisoCodigo
        )
    }

    private fun camara() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Imagen")
            put(MediaStore.Images.Media.DESCRIPTION, "camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, permisoCamara)
    }

    private fun galeria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, permisoGaleria)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                permisoCamara -> {
                    imageView.setImageURI(imageUri)
                }
                permisoGaleria -> {
                    val Uriselect: Uri? = data?.data
                    if (Uriselect != null) {
                        imageUri = Uriselect
                        imageView.setImageURI(Uriselect)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permisoCodigo) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permiso Aceptado.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso Denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

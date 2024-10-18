package com.example.taller2.Logica

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.taller2.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val botonContactos = findViewById<ImageButton>(R.id.contactos)
        val botonCamara = findViewById<ImageButton>(R.id.camara)
        val botonMapa = findViewById<ImageButton>(R.id.mapa)

        botonContactos.setOnClickListener{
            val intent = Intent(this, Contactos::class.java)
            startActivity(intent)
        }

        botonCamara.setOnClickListener{
            val intent = Intent(this, Camara::class.java)
            startActivity(intent)
        }

        botonMapa.setOnClickListener{
            val intent = Intent(this, MapaConGoogle ::class.java )
            startActivity(intent)
        }


    }
}
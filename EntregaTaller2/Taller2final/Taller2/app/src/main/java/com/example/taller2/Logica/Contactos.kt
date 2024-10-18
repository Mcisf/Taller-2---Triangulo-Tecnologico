package com.example.taller2.Logica

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.taller2.Datos.Datos
import com.example.taller2.R

class Contactos : AppCompatActivity() {

    var mProjection: Array<String>?=null
    var mCursor: Cursor? = null
    var mContactsAdapter: ContactsAdapter? = null
    var mlista: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactos)

        val permisos = arrayOf(Manifest.permission.READ_CONTACTS)
        mlista = findViewById<ListView>(R.id.list)
        mProjection = arrayOf(ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME_PRIMARY)
        mContactsAdapter = ContactsAdapter(this, null, 0)
        mlista?.adapter = mContactsAdapter

        pedirPermiso(this, permisos,"Se necesitan estos permisos para todas las funcionalidades de la app",
            Datos.MY_PERMISSION_REQUEST_READ_CONTATCS)

    }

    fun pedirPermiso(context: Activity, permisos: Array<String>, justificacion: String, idCode: Int) {

        if(ContextCompat.checkSelfPermission(context,permisos[0]) != PackageManager.PERMISSION_GRANTED ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permisos[0])) {
                Toast.makeText(this, "se necesita permiso para esta funcion", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(context, arrayOf(permisos[0]), idCode)
            }


            ActivityCompat.requestPermissions(context, permisos, idCode)
        }
        else{
            initView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Datos.MY_PERMISSION_REQUEST_READ_CONTATCS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "!Gracias", Toast.LENGTH_SHORT).show()
                    initView()

                } else {
                    Toast.makeText(this, "Funcionalidades Limitadas", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {

            }
        }
    }

    fun initView() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            mCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, mProjection, null, null, null
            )
            mContactsAdapter?.changeCursor(mCursor)
        }
    }
}

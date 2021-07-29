package com.example.botondepanicov1

import android.Manifest
import android.accounts.AccountManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_registro.*


class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        title = "REGISTRO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        inicializar_spinner_documento()
        inicializar_spinner_genero()
        inicializar_spinner_rh()
        inicializar_spinner_signo()
    }

    fun inicializar_spinner_signo(){
        val spinner_signo = findViewById<Spinner>(R.id.signo)
        val lista_signo = resources.getStringArray(R.array.signo)
        val adaptador_signo =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lista_signo)

        spinner_signo.adapter = adaptador_signo

        gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Toast.makeText(this@Registro, lista_signo[position], Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun inicializar_spinner_rh(){
        val spinner_rh = findViewById<Spinner>(R.id.rh)
        val lista_rh = resources.getStringArray(R.array.rh)
        val adaptador_rh =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lista_rh)

        spinner_rh.adapter = adaptador_rh

        gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Toast.makeText(this@Registro, lista_rh[position], Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun inicializar_spinner_genero(){
        val spinner_genero = findViewById<Spinner>(R.id.gender)
        val lista_genero = resources.getStringArray(R.array.genero)
        val adaptador_genero =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lista_genero)

        spinner_genero.adapter = adaptador_genero

        gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Toast.makeText(this@Registro, lista_genero[position], Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun inicializar_spinner_documento(){
        val spinner_documento = findViewById<Spinner>(R.id.document_type)
        val lista_docuemnto = resources.getStringArray(R.array.tipos_documento)
        val adaptador_docuemnto =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lista_docuemnto)

        spinner_documento.adapter = adaptador_docuemnto

        document_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                Toast.makeText(this@Registro, lista_docuemnto[position], Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
package com.example.botondepanicov1


import android.content.Intent
import android.os.Bundle

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.error_password
import kotlinx.android.synthetic.main.activity_registro.password


class Registro : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        title = "REGISTRO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        inicializarSpinnerDocumento()
        inicializarSpinnerGenero()
        inicializarSpinnerRh()
        inicializarSpinnerSigno()
    }

    fun onClickRegistrar(v: View) {
        //CONTROLAR QUE SI HAY ERROR, NO PASE A AUTENTIFICAR
        if (falloRegistro()) {
            autentificar()
        }
    }

    private fun autentificar() {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    showAlet1()
                }
            }
    }

    private fun falloRegistro(): Boolean {
        var resultado = true
        // Validcion campo email nonull
        if (email.text.toString().isEmpty()) {
            error_email.text = ("Ingrese su correo electrónico ")
            error_email.error = ""
            resultado = false
        } else {
            error_email.text = null
            error_email.error = null
        }
        // Validcion campo password nonull
        if (password.text.toString().isEmpty()) {
            error_password.text = ("Ingrese la contraseña ")
            error_password.error = ""
            resultado = false
        } else {
            error_password.text = null
            error_password.error = null
        }
        // Validcion campo password_confirmation nonull
        when {
            password_confirmation.text.toString().isEmpty() -> {
                error_confirmation.text = ("Ingrese la contraseña ")
                error_confirmation.error = ""
            }
            password_confirmation.text.toString() != password.text.toString() -> {
                error_password.text = ("Las contraseñas deben de ser iguales ")
                error_password.error = ""
                error_confirmation.text = ("Las contraseñas deben de ser iguales ")
                error_confirmation.error = ""

            }
            else -> {
                error_confirmation.text = null
                error_confirmation.error = null
            }
        }
        //validacion spinner documento
        if (document_type.selectedItem == "Seleccione") {
            error_document_type.text = ("Seleccione una opción ")
            error_document_type.error = ""
            resultado = false
        } else {
            error_document_type.text = null
            error_document_type.error = null
        }
        //Validacion del campo numero documento
        if (document_number.text.toString().isEmpty()) {
            error_document_number.text = ("Ingrese su número de documento ")
            error_document_number.error = ""
            resultado = false
        } else {
            error_document_number.text = null
            error_document_number.error = null
        }
        //Validacion del campo nombre
        if (first_name.text.toString().isEmpty()) {
            error_first_name.text = ("Ingrese sus nombres ")
            error_first_name.error = ""
            resultado = false
        } else {
            error_first_name.text = null
            error_first_name.error = null
        }
        //Validacion del campo apellido
        if (last_name.text.toString().isEmpty()) {
            error_last_name.text = ("Ingrese sus apellidos ")
            error_last_name.error = ""
            resultado = false
        } else {
            error_last_name.text = null
            error_last_name.error = null
        }
        //validacion spinner genero
        if (gender.selectedItem == "Seleccione") {
            error_gender.text = ("Seleccione una opción ")
            error_gender.error = ""
            resultado = false
        } else {
            error_gender.text = null
            error_gender.error = null
        }
        //validacion spinner rh
        if (rh.selectedItem == "Seleccione") {
            error_rh.text = ("Seleccione una opción ")
            error_rh.error = ""
            resultado = false
        } else {
            error_rh.text = null
            error_rh.error = null
        }
        //Validacion del campo fecha nacimiento
        if (fecha_nacimiento.text.toString().isEmpty()) {
            error_fecha_nacimiento.text = ("Ingrese su fecha de nacimiento ")
            error_fecha_nacimiento.error = ""
            resultado = false
        } else {
            error_fecha_nacimiento.text = null
            error_fecha_nacimiento.error = null
        }

        return resultado
    }

    private fun showAlet1() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Datos erroneos o ya se ha verificado una cuenta con éste Email")
        builder.setPositiveButton("Aceptar", null)
        val dialogo: AlertDialog = builder.create()
        dialogo.show()
    }

    private fun inicializarSpinnerSigno() {
        val spinnerSigno = findViewById<Spinner>(R.id.signo)
        val listaSigno = resources.getStringArray(R.array.signo)
        val adaptadorSigno =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaSigno)

        spinnerSigno.adapter = adaptadorSigno
    }

    private fun inicializarSpinnerRh() {
        val spinnerRh = findViewById<Spinner>(R.id.rh)
        val listaRh = resources.getStringArray(R.array.rh)
        val adaptadorRh =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaRh)

        spinnerRh.adapter = adaptadorRh
    }

    private fun inicializarSpinnerGenero() {
        val spinnerGenero = findViewById<Spinner>(R.id.gender)
        val listaGenero = resources.getStringArray(R.array.genero)
        val adaptadorGenero =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaGenero)

        spinnerGenero.adapter = adaptadorGenero
    }

    private fun inicializarSpinnerDocumento() {
        val spinnerDocumento = findViewById<Spinner>(R.id.document_type)
        val listaDocumento = resources.getStringArray(R.array.tipos_documento)
        val adaptadorDocumento =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaDocumento)

        spinnerDocumento.adapter = adaptadorDocumento
    }
}
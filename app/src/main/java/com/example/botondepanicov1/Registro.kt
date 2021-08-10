package com.example.botondepanicov1


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log

import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.auth.*

import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.error_password
import kotlinx.android.synthetic.main.activity_registro.password


class Registro : AppCompatActivity() {

    private lateinit var dbReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var email : String

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "REGISTRO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        inicializarSpinnerDocumento()
        inicializarSpinnerGenero()
        inicializarSpinnerRh()
        inicializarSpinnerSigno()

        database= FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        dbReference=database.reference.child("User")
    }

    @SuppressLint("SetTextI18n")
    fun onClickCalendario(v:View){
        val c = Calendar.getInstance()
        val dia = c.get(Calendar.DAY_OF_MONTH)
        val mes = c.get(Calendar.MONTH)
        val anio = c.get(Calendar.YEAR)
        val date = DatePickerDialog( this,
            { view, year, month, dayOfMonth ->
                fecha_nacimiento.setText("$dayOfMonth / ${month + 1} / $year")
                Log.v("Sergio","2 $dia $mes $anio")
            },anio,mes,dia)
        date.show()
    }

    fun onClickRegistrar(v: View) {
        //CONTROLAR QUE SI HAY ERROR, NO PASE A AUTENTIFICAR
        if (falloRegistro()) {
            autentificar()
        }
    }

    private fun crearNuevaCuenta(){
        val tipoDocumento = document_type.selectedItem.toString()
        val documento = document_number.text.toString()
        val nombre = first_name.text.toString()
        val apellido = last_name.text.toString()
        val genero = gender.selectedItem.toString()
        val rh = rh.selectedItem.toString() + signo.selectedItem.toString()
        val fecha = fecha_nacimiento.text.toString()
        val contraseña = password.text.toString()
            auth.createUserWithEmailAndPassword(email,contraseña).addOnCompleteListener(this){
                    task ->
                if(task.isComplete){
                    val userBD= dbReference.child(documento)
                    userBD.child("tipo_documento").setValue(tipoDocumento)
                    userBD.child("documento").setValue(documento)
                    userBD.child("nombre").setValue(nombre)
                    userBD.child("apellido").setValue(apellido)
                    userBD.child("genero").setValue(genero)
                    userBD.child("rh").setValue(rh)
                    userBD.child("fecha_nacimiento").setValue(fecha)
                    userBD.child("contraseña").setValue(contraseña)
                }
            }
    }


    private fun autentificar() {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    crearNuevaCuenta()
                   val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                } else {
                    showAlet1()
                }
            }
    }

    private fun falloRegistro(): Boolean {
        var resultado = true
        email=""
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
            email = document_type.selectedItem.toString()+document_number.text.toString() + "@gmail.com"
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
        builder.setMessage("Datos erroneos o ya se ha verificado una cuenta con este documento")
        builder.setPositiveButton("Aceptar", null)
        val dialogo: AlertDialog = builder.create()
        dialogo.show()
    }
    private fun showAlet2() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Datos erroneos o ya se ha verificado una cuenta con ta cedula")
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
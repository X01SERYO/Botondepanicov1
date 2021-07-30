package com.example.botondepanicov1


import android.content.Intent
import android.os.Bundle

import android.util.Log

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_registro.*
import java.util.*
import com.example.botondepanicov1.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registro.error_password
import kotlinx.android.synthetic.main.activity_registro.password


class Registro : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        title = "REGISTRO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        /////////////////////////////////////////////////////////////////////////////////////////

        val email= email.text.toString()
        val contraseña = password.text.toString()


        registrar.setOnClickListener(){
            autentificar()
        }

       //////////////////////////////////////////////////////////////////////////////////////////
        inicializar_spinner_documento()
        inicializar_spinner_genero()
        inicializar_spinner_rh()
        inicializar_spinner_signo()
    }

    fun autentificar(){
        if( email.text!!.isNotEmpty() &&password_confirmation.text!!.isNotEmpty() &&  password.text!!.isNotEmpty() && password_confirmation.text.toString()==password.text.toString()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(),password.text.toString()).addOnCompleteListener {

                if(it.isSuccessful) {
                    Log.d("user", "creado con exito el ${it.result?.user?.uid}")
                   // return@addOnCompleteListener

                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)

                }
                else{
                    showAlet1()
                }
            }
        }else{

            falloRegistro()

        }


    }

    fun falloRegistro() {

        if (email.text.toString().isEmpty()) {
            error_email.text = ("El Email es necesario")
            error_email.error = ""
        } else {
            error_email.text = null
            error_email.error = null
        }
        if (password.text.toString().isEmpty()) {
            error_password.text = ("La contraseña es necesaria")
            error_password.error = ""
        } else {
            error_password.text = null
            error_password.error = null
        }
        if (password_confirmation.text.toString().isEmpty()) {
            error_confirmation.text = ("La contraseña es necesaria")
            error_confirmation.error = ""
        } else if(password_confirmation.text.toString()!=password.text.toString()){
            error_password.text = ("Las contraseñas deben de ser iguales")
            error_password.error = ""
            error_confirmation.text = ("Las contraseñas deben de ser iguales")
            error_confirmation.error = ""

        }
        else {
            error_confirmation.text = null
            error_confirmation.error = null
        }
        /*if (password_confirmation.text.toString().isNotEmpty()!=password.text.toString().isNotEmpty()) {
            error_password.text = ("Las contraseñas deben de ser iguales")
            error_password.error = ""
            error_confirmation.text = ("Las contraseñas deben de ser iguales")
            error_confirmation.error = ""
        } else {
            error_password.text = null
            error_password.error = null
            error_confirmation.text = null
            error_confirmation.error = null
        }*/

    }



    private fun showAlet1() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Datos erroneos o ya se ha verificado una cuenta con éste Email")
        builder.setPositiveButton("Aceptar",null)
        val dialogo: AlertDialog = builder.create()
        dialogo.show()
    }





    fun inicializar_spinner_signo(){
        val spinner_signo = findViewById<Spinner>(R.id.signo)
        val lista_signo = resources.getStringArray(R.array.signo)
        val adaptador_signo =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lista_signo)

        spinner_signo.adapter = adaptador_signo

        signo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        rh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
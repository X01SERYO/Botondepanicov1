package com.example.botondepanicov1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.error_password
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_registro.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        title = "BOTÓN DE PÁNICO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    fun iniciarSesion(v: View){
        //CONTROLAR QUE SI HAY ERROR, NO PASE A AUTENTIFICAR
        if (falloInisioDeSesion()){
            validacionFirebase()
        }
    }

    private fun validacionFirebase(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(user.text.toString(),password.text.toString()).addOnCompleteListener {
            FirebaseDatabase.getInstance().getReference("/User")
            if(it.isSuccessful){
                val intent = Intent(this,PantallaPrincipal::class.java)
                startActivity(intent)
            }else {
                credencialesInvalidas()
            }
        }
    }

    private fun credencialesInvalidas(){
        if (ComprobacionCredenciales().validacion(user.text.toString(), password.text.toString())) {
            error_validacion.text = null
            error_validacion.error = null
            startActivity(intent)
        } else {
            error_validacion.text = ("Credenciales invalidas")
            error_validacion.error = ""
        }
    }

    private fun falloInisioDeSesion(): Boolean {
        var resultado = true
        if (user.text.toString().isEmpty()) {
            error_user.text = ("El usuario es necesario")
            error_user.error = ""
            resultado = false
        } else {
            error_user.text = null
            error_user.error = null
        }
        if (password.text.toString().isEmpty()) {
            error_password.text = ("La contraseña es necesaria")
            error_password.error = ""
            resultado = false
        } else {
            error_password.text = null
            error_password.error = null
        }
        return resultado
    }

    fun onClickInicarSesionSinCredenciales(v: View) {
        val intent = Intent(this, PantallaPrincipal::class.java)
        startActivity(intent)
    }

    fun onClickRegistro(v:View){
        val intent = Intent(this,Registro::class.java)
        startActivity(intent)
    }
}
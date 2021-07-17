package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun onClickInicarSesion(v: View) {
        val intent = Intent(this, PantallaPrincipal::class.java)
        if (user.text.toString().isEmpty()) {
            error_user.text = ("El usuario es necesario")
            error_user.error = ""
        } else {
            error_user.text = null
            error_user.error = null
        }
        if (password.text.toString().isEmpty()) {
            error_password.text = ("La contraseña es necesaria")
            error_password.error = ""
        } else {
            error_password.text = null
            error_password.error = null
        }
        if (ComprobacionCredenciales().validacion(user.text.toString(), password.text.toString())) {
            startActivity(intent)
            error_validacion.text = null
            error_validacion.error = null
        } else {
            error_validacion.text = ("Credenciales invalidas")
            error_validacion.error = ""
        }
    }

    fun onClickInicarSesionSinCredenciales(v: View) {
        val intent = Intent(this, PantallaPrincipal::class.java)
        startActivity(intent)
    }
}
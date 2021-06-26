package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun onClickInicarSesion(v:View){
        val intent = Intent(this,PantallaPrincipal::class.java)

        startActivity(intent)
    }

    fun onClickInicarSesionSinCredenciales(v:View){
        val intent = Intent(this,PantallaPrincipal::class.java)
        startActivity(intent)
    }
}
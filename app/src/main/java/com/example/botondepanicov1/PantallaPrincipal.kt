package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class PantallaPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)
    }

    fun onClickSolicitarAyuda(v:View){
        val intent = Intent(this,SolicitandoAyuda::class.java)
        startActivity(intent)
    }

    fun onClickRecibirAlertas(v:View){
        val intent = Intent(this,RecibirAlertas::class.java)
        startActivity(intent)
    }
}
package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SolicitandoAyuda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitando_ayuda)
    }

    fun onClickCandcelarAyuda(v: View){
        val intent = Intent(this,PantallaPrincipal::class.java)
        startActivity(intent)
    }
}
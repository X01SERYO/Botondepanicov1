package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceManager
import android.util.Log
import android.view.View

class SolicitandoAyuda : AppCompatActivity() {

    private var key :String = "MY_KEY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitando_ayuda)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        Log.v("Sergio", pref.getString(key,"No hay datos").toString())
    }

    fun onClickCandcelarAyuda(v: View){
        val intent = Intent(this,PantallaPrincipal::class.java)
        startActivity(intent)
    }
}
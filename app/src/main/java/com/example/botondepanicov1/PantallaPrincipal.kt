package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.botondepanicov1.wifi_direct.BuscandoDispositivosWifi
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager


class PantallaPrincipal : AppCompatActivity() {
    private var key: String = "MY_KEY"
    private var keyLogin: String = "LOGIN"
    private var keyAlarma: String = "ALARMA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_principal)


        estadoAlarma()
    }

    fun estadoAlarma(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString(keyAlarma, "Inactiva")
        editor.apply()
    }

    fun onClickSolicitarAyuda(v:View){
        val intent = Intent(this, BuscandoDispositivosWifi::class.java)
        startActivity(intent)
    }

    fun onClickCerrarSesion(v:View){
        finish()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = prefs.edit()
        editor.putString(key, "No hay datos")
        editor.apply()

        editor = prefs.edit()
        editor.putString(keyLogin, "No hay datos")
        editor.apply()

        editor = prefs.edit()
        editor.putString(keyAlarma, "No hay datos")
        editor.apply()

        val intent = Intent(this,Login::class.java)
        startActivity(intent)
    }
}
package com.example.botondepanicov1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.botondepanicov1.wifi_direct.BuscandoDispositivosWifi
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager
import android.util.Log


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

        Log.v("Sergio","Pref a " + prefs.getString(key,"1").toString())
        Log.v("Sergio","Pref a" + prefs.getString(keyLogin,"2").toString())
        Log.v("Sergio","Pref a" + prefs.getString(keyAlarma,"3").toString())

        val editor = prefs.edit()
        editor.clear().apply()

        Log.v("Sergio","Pref d" + prefs.getString(key,"1").toString())
        Log.v("Sergio","Pref d" + prefs.getString(keyLogin,"2").toString())
        Log.v("Sergio","Pref d" + prefs.getString(keyAlarma,"3").toString())

        val intent = Intent(this,Login::class.java)
        startActivity(intent)
    }
}
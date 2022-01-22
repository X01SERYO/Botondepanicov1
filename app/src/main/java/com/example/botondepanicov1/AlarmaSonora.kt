package com.example.botondepanicov1

import android.content.Context
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import com.example.botondepanicov1.wifi_direct.BuscandoDispositivosWifi

class AlarmaSonora {
    //Varibale para guardar el estado de la alarme en las referencias
    private var keyAlarma: String = "ALARMA"
    //finaliza el sonido de la alrma sonora
    fun apagarTemporizador(playPausar:Button, mp:MediaPlayer, context: Context){
        mp.stop()
        playPausar.text = ("ACTIVAR ALARMA")
    }
    //Cuando se cambia de una actividad o se recarga la misma, apaga el sonido, guarda el estado en que esta la alarma
    fun apagarFinActividad(playPausar:Button, mp:MediaPlayer, context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()

        mp.stop()
        playPausar.text = ("ACTIVAR ALARMA")
        editor.putString(keyAlarma, "Inactiva")
        editor.apply()
    }
    //Se encarga de encender o apagar la alarma segun el estado en el que se encuentre
    fun reproducirParar(playPausar:Button, mp:MediaPlayer, context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()

        Log.d("Sergio","INCIO fun reproducirParar " + prefs.getString(keyAlarma,"No hay datos").toString())
        if (mp.isPlaying){
            mp.pause()
            playPausar.text = ("ACTIVAR ALARMA")

            editor.putString(keyAlarma, "Inactiva")
            editor.apply()
        }else{
            mp.start()
            playPausar.text = ("DESACTIVAR ALARMA")

            editor.putString(keyAlarma, "Activa")
            editor.apply()
        }
    }
    //Lee la preferencia para saber el estado de la alarma
    fun estadoPreferencia(playPausar:Button, mp:MediaPlayer, context: Context){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        Log.d("Sergio","INCIO fun estadoPreferencia " + prefs.getString(keyAlarma,"No hay datos").toString())
        if (prefs.getString(keyAlarma,"No hay datos").toString() == "Activa"){
            mp.start()
            playPausar.text = ("DESACTIVAR ALARMA")
        }
    }
}
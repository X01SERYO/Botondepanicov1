package com.example.botondepanicov1

import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import com.example.botondepanicov1.wifi_direct.BuscandoDispositivosWifi

class AlarmaSonora {

    private var keyAlarma: String = "ALARMA"

    fun apagarTemporizador(playPausar:Button, mp:MediaPlayer, context: BuscandoDispositivosWifi){
        mp.stop()
        playPausar.text = ("ACTIVAR ALARMA SONORA")
    }

    fun apagarFinActividad(playPausar:Button, mp:MediaPlayer, context: BuscandoDispositivosWifi){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()

        mp.stop()
        playPausar.text = ("ACTIVAR ALARMA SONORA")
        editor.putString(keyAlarma, "Inactiva")
        editor.apply()
    }

    fun reproducirParar(playPausar:Button, mp:MediaPlayer, context: BuscandoDispositivosWifi){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()

        Log.d("Sergio","INCIO fun reproducirParar " + prefs.getString(keyAlarma,"No hay datos").toString())
        if (mp.isPlaying){
            mp.pause()
            playPausar.text = ("ACTIVAR ALARMA SONORA")

            editor.putString(keyAlarma, "Inactiva")
            editor.apply()
        }else{
            mp.start()
            playPausar.text = ("DESACTIVAR ALARMA SONORA")

            editor.putString(keyAlarma, "Activa")
            editor.apply()
        }
    }

    fun estadoPreferencia(playPausar:Button, mp:MediaPlayer, context: BuscandoDispositivosWifi){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        Log.d("Sergio","INCIO fun estadoPreferencia " + prefs.getString(keyAlarma,"No hay datos").toString())
        if (prefs.getString(keyAlarma,"No hay datos").toString() == "Activa"){
            mp.start()
            playPausar.text = ("DESACTIVAR ALARMA SONORA")
        }
    }
}
package com.example.botondepanicov1

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog

class PopUpAlerta {

    fun mostrarErrorRegistro(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Ya se ha registrado anteriormente con este documento")
        builder.setPositiveButton("Aceptar", { _: DialogInterface, i: Int ->
        })
        builder.show()

    }

    fun mostrarExitoRegistro(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Gracias por confiar")
        builder.setMessage("Registro Exitoso")
        builder.setPositiveButton("Aceptar", { _: DialogInterface, i: Int ->
        })
        builder.show()
    }
}
package com.example.botondepanicov1

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class PopUpAlerta {

    fun mostrarAlertaRegistro(context: Context) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Error")
        builder.setMessage("Ya se ha registrado anteriormente con este documento")
        builder.setPositiveButton("Aceptar", { _: DialogInterface, i: Int ->
        })
        builder.show()

    }
}
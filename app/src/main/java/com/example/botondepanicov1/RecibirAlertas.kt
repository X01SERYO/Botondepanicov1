package com.example.botondepanicov1

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_recibir_alertas.*


class RecibirAlertas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recibir_alertas)

        popUp.setOnClickListener {
            val window = PopupWindow(this)
            val view = layoutInflater.inflate(R.layout.popup,null)
            window.contentView = view

            val boton = view.findViewById<Button>(R.id.boton)
            boton.setOnClickListener{
                window.dismiss()
            }
            window.showAsDropDown(popUp)
        }

    }


}
package com.example.botondepanicov1

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.error_password
import kotlinx.android.synthetic.main.activity_login.password

class Login : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "BOTÓN DE PÁNICO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


// ...
// Initialize Firebase Auth
        auth = Firebase.auth



    }


    fun iniciarSesion(v: View){
        //CONTROLAR QUE SI HAY ERROR, NO PASE A AUTENTIFICAR
        if (falloInisioDeSesion()){
            validacionFirebase()
            //nuevaValidación()

        }
    }



    private fun validacionFirebase(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password.text.toString()).addOnCompleteListener {
            FirebaseDatabase.getInstance().getReference("/User")
            if(it.isSuccessful){
                val uid = FirebaseAuth.getInstance().uid
                Log.d("variable", "$uid")
                val intent = Intent(this,PantallaPrincipal::class.java)
                startActivity(intent)
            }else {
                credencialesInvalidas()
            }
        }
    }

    private fun credencialesInvalidas(){

            error_validacion.text = ("Credenciales invalidas")
            error_validacion.error = ""

    }

    private fun falloInisioDeSesion(): Boolean {
        var resultado = true
        if (user.text.toString().isEmpty()) {
            error_user.text = ("El usuario es necesario")
            error_user.error = ""
            resultado = false
        } else {
            email=user.text.toString()+"@gmail.com"
            error_user.text = null
            error_user.error = null
        }
        if (password.text.toString().isEmpty()) {
            error_password.text = ("La contraseña es necesaria")
            error_password.error = ""
            resultado = false
        } else {
            error_password.text = null
            error_password.error = null
        }
        return resultado
    }

    fun onClickInicarSesionSinCredenciales(v: View) {
        val intent = Intent(this, PantallaPrincipal::class.java)
        startActivity(intent)
    }

    fun onClickRegistro(v:View){
        val intent = Intent(this,Registro::class.java)
        startActivity(intent)
    }
}
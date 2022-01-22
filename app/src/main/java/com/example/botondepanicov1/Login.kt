package com.example.botondepanicov1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.error_password
import kotlinx.android.synthetic.main.activity_login.password

class Login : AppCompatActivity() {
    //Variable utilziada para el logueo
    lateinit var auth: FirebaseAuth
    //Variable para guardar el correo
    private lateinit var email: String
    //Variable para guardar en las preferecnias el logueo auto
    private var key: String = "MY_KEY"
    private var keyLogin: String = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        title = "BOTÓN DE PÁNICO"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Validacon perisos ubicacion
        val validacionPermisos = ValidacionPermisos()
        validacionPermisos.validacionUbicacion(this)
        //Llama la funcion
        iniciarSesionAutomatico()
        inicializarSpinnerDocumento()
        //Intancia el objeto de autenticar
        auth = Firebase.auth
    }
    //Captura los datos personales descargados de FIREBASE para guardar en las prefencias
    private fun capturarDatosPersona() {
        val mDatabase = FirebaseDatabase.getInstance().reference
        val documento = email.replace("@gmail.com","")
        val persona = Persona()
        mDatabase.child("User").child(documento).get().addOnSuccessListener {
            persona.setTipoDocumento(it.child("tipo_documento").value.toString())
            persona.setNumeroDocumento(it.child("documento").value.toString())
            persona.setNombres(it.child("nombre").value.toString())
            persona.setApellidos(it.child("apellido").value.toString())
            persona.setGenero(it.child("genero").value.toString())
            persona.setRh(it.child("rh").value.toString())
            persona.setFechaNacimiento(it.child("fecha_nacimiento").value.toString())

            Log.v("Sergio", persona.concatenado())
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putString(key, persona.concatenado())
            editor.apply()
        }
    }
    //Guarda las credecnailes en preferencias
    fun capturarCredenciales(email: String, contrasenia: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString(keyLogin, "$email;$contrasenia")
        editor.apply()
    }
    //Si ya ha iniciado ssesion, llama el metodo para no solictar de nuevo las credenciales
    fun iniciarSesionAutomatico() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val datos = pref.getString(keyLogin, "No hay datos").toString()
        Log.v("Sergio", datos)
        if (datos != "No hay datos") {
            finish()
            val intent = Intent(this, PantallaPrincipal::class.java)
            startActivity(intent)
        }
    }
    //solicita las credenciales para iniicar sesion por primera vez
    fun iniciarSesion(v: View) {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        //Valida la conexion a internet
        if (isConnected){
            //CONTROLAR QUE SI HAY ERROR, NO PASE A AUTENTIFICAR
            if (falloInisioDeSesion()) {
                validacionFirebase()
            }
        } else {
            mostrarErrorConexion()
        }
    }
    //sms si no hay conexion a internet
    private fun mostrarErrorConexion() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Verifique su conexión a internet ")
            .setPositiveButton("Aceptar") { _, _ ->
            }
            .create()
        dialog.show()
    }
    //valida que el usuario este creado en FIREBASE
    private fun validacionFirebase() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password.text.toString())
            .addOnCompleteListener {
                FirebaseDatabase.getInstance().getReference("/User")
                if (it.isSuccessful) {
                    capturarDatosPersona()
                    capturarCredenciales(email, password.text.toString())
                    finish()
                    val intent = Intent(this, PantallaPrincipal::class.java)
                    startActivity(intent)
                } else {
                    credencialesInvalidas()
                }
            }
    }
    //sms por si el usuario y contraseña no coincide
    private fun credencialesInvalidas() {
        error_validacion.text = ("Credenciales invalidas")
        error_validacion.error = ""
    }
    //validacion de campos vacios
    private fun falloInisioDeSesion(): Boolean {
        var resultado = true
        email = ""
        //validacion spinner documento
        if (document_type_login.selectedItem == "Seleccione") {
            error_document_type_login.text = ("Seleccione una opción ")
            error_document_type_login.error = ""
            resultado = false
        } else {
            error_document_type_login.text = null
            error_document_type_login.error = null
        }
        // valicaion campo numero de documento
        if (user.text.toString().isEmpty()) {
            error_user.text = ("El documento es necesario")
            error_user.error = ""
            resultado = false
        } else {
            email =
                document_type_login.selectedItem.toString() + user.text.toString() + "@gmail.com"
            error_user.text = null
            error_user.error = null
        }
        //valicion campo contraseña
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
    //inicializa una lista deplegable del tipo documento
    private fun inicializarSpinnerDocumento() {
        val spinnerDocumento = findViewById<Spinner>(R.id.document_type_login)
        val listaDocumento = resources.getStringArray(R.array.tipos_documento)
        val adaptadorDocumento =
            ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, listaDocumento)

        spinnerDocumento.adapter = adaptadorDocumento
    }
    //captura accion boton de iniciar seion sin crenciales
    fun onClickInicarSesionSinCredenciales(v: View) {
        val intent = Intent(this, PantallaPrincipal::class.java)
        startActivity(intent)
    }
    //captura accion boton de  registro
    fun onClickRegistro(v: View) {
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
    }
}
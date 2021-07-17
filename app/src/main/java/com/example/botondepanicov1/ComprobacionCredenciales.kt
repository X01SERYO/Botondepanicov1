package com.example.botondepanicov1

class ComprobacionCredenciales {
    fun validacion(user :String,password:String):Boolean{
        val user_admin = "ssilva25"
        val password_admin = "123456"
        if (user == user_admin && password == password_admin){
            return true
        }
        return false
    }
}
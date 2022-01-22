package com.example.botondepanicov1

//CLase para el  objeto persona
class Persona {
    //Atributos del objeto
    var tipoDocumento: String = ""
    var numeroDocumento: String = ""
    var nombres: String = ""
    var apellidos: String = ""
    var genero: String = ""
    var rh: String = ""
    var fechaNacimiento: String = ""
    var contrasenia: String = ""

    //GETTERS AND SETTERS
    @JvmName("getTipoDocumento1")
    fun getTipoDocumento(): String {
        return this.tipoDocumento
    }

    @JvmName("setTipoDocumento1")
    fun setTipoDocumento(tipoDocumento: String) {
        this.tipoDocumento = tipoDocumento
    }


    @JvmName("getNumeroDocumento1")
    fun getNumeroDocumento(): String {
        return this.numeroDocumento
    }

    @JvmName("setNumeroDocumento1")
    fun setNumeroDocumento(numeroDocumento: String) {
        this.numeroDocumento = numeroDocumento
    }

    @JvmName("getNombres1")
    fun getNombres(): String {
        return this.nombres
    }

    @JvmName("setNombres1")
    fun setNombres(nombres: String) {
        this.nombres = nombres
    }

    @JvmName("getApellidos1")
    fun getApellidos(): String {
        return this.apellidos
    }

    @JvmName("setApellidos1")
    fun setApellidos(apellidos: String) {
        this.apellidos = apellidos
    }

    @JvmName("getGenero1")
    fun getGenero(): String {
        return this.genero
    }

    @JvmName("setGenero1")
    fun setGenero(genero: String) {
        this.genero = genero
    }

    @JvmName("getRh1")
    fun getRh(): String {
        return this.rh
    }

    @JvmName("setRh1")
    fun setRh(rh: String) {
        this.rh = rh
    }

    @JvmName("getFechaNacimiento1")
    fun getFechaNacimiento(): String {
        return this.fechaNacimiento
    }

    @JvmName("setFechaNacimiento1")
    fun setFechaNacimiento(fechaNacimiento: String) {
        this.fechaNacimiento = fechaNacimiento
    }

    @JvmName("getContrasenia1")
    fun getContrasenia(): String {
        return this.contrasenia
    }

    @JvmName("setContrasenia1")
    fun setContrasenia(contrasenia: String) {
        this.contrasenia = contrasenia
    }

    //Concatena los datos para envairlos
    fun concatenado(): String {
        return "Tipo de documento: $tipoDocumento \nNúmero de documento: $numeroDocumento " +
                "\nNombre: $nombres \nApellido: $apellidos \nGénero: $genero \nTipo de sangre: " +
                "$rh \nFecha de nacimiento: $fechaNacimiento"
    }
}
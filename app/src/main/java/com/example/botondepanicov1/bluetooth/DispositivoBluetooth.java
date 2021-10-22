package com.example.botondepanicov1.bluetooth;

import java.text.DecimalFormat;

public class DispositivoBluetooth {

    String nombre;
    Double distancia;
    String fecha;

    public DispositivoBluetooth(String nombre, Double distancia, String fecha) {
        this.nombre = nombre;
        this.distancia = distancia;
        this.fecha = fecha;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getDistancia() {
        DecimalFormat formato2 = new DecimalFormat("#.##");
        return Double.valueOf(formato2.format(distancia));
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

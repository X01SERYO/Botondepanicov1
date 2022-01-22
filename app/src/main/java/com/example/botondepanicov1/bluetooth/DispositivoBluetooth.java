package com.example.botondepanicov1.bluetooth;

import org.altbeacon.beacon.Identifier;

import java.text.DecimalFormat;

// clase del objeto Bluetooth
public class DispositivoBluetooth {

    Identifier nombre;
    Double distancia;
    String fecha;

    public DispositivoBluetooth(Identifier nombre, Double distancia, String fecha) {
        this.nombre = nombre;
        this.distancia = distancia;
        this.fecha = fecha;
    }

    public Identifier getNombre() {
        return nombre;
    }

    public void setNombre(Identifier nombre) {
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

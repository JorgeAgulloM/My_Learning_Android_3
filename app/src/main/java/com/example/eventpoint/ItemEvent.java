package com.example.eventpoint;

public class ItemEvent {
    //Clase para crear cada evento.

    //Atributos
    private String name; //Nombre del evento.
    private final String date; //Fecha y hora del evento.
    private final String location; //Localización del evento.
    private final double latPoint; //Latitud.
    private final double lngPoint; //Longitud.

    //Constructor
    public ItemEvent(String name, String date, String location, double latPoint, double lngPoint) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.latPoint = latPoint;
        this.lngPoint = lngPoint;
    }

    //Getters y Setter.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public double getLatPoint() {
        return latPoint;
    }

    public double getLngPoint() {
        return lngPoint;
    }


}

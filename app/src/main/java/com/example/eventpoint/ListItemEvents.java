package com.example.eventpoint;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ListItemEvents {
    //Clase apara almacenar los eventos.

    //Atributo
    public ArrayList<ItemEvent> allEvents;

    //Constructor
    public ListItemEvents() {
        allEvents = new ArrayList<>();
    }

    //Traductor json para almacenar los eventos en sharepreferences
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    //Traductor json para recuperar los eventos guardados en sharepreferences
    public ListItemEvents fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ListItemEvents.class);
    }
}

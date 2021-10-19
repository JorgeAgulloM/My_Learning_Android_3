package com.example.eventpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterEvents extends ArrayAdapter {

    //Atributos
    Context context;
    int item;
    ArrayList<ItemEvent> events;

    //Constructor
    public AdapterEvents(@NonNull Context context, int resource, @NonNull ArrayList<ItemEvent> events) {
        super(context, resource, events);
        this.context = context;
        this.item = resource;
        this.events = events;
    }

    //Metodo para inflar la vista.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Si la vista es null se crea el inflater en convertView
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(item, parent, false);
        }

        //Se declaran y enlazan las variables a los objetos xml de item_map.xml
        MaterialTextView txtViewnameEvent = convertView.findViewById(R.id.txtViewnameEvent);
        txtViewnameEvent.setText(events.get(position).getName());

        MaterialTextView txtViewdateEvent = convertView.findViewById(R.id.txtViewdateEvent);
        txtViewdateEvent.setText(events.get(position).getDate());

        ImageView imageViewEvent = convertView.findViewById(R.id.imageViewEvent);
        imageViewEvent.setTag(events.get(position).getLocation());

        ImageView deleteEvent = convertView.findViewById(R.id.deleteEvent);
        deleteEvent.setOnClickListener(v -> {
            ((MainMapsActivity) Objects.requireNonNull(getContext())).deleteEvent(position);
        });

        return convertView;
    }

}

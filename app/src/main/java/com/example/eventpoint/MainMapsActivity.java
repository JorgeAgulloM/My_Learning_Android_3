package com.example.eventpoint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import java.util.ArrayList;
import java.util.Calendar;

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnMarkerClickListener {

    //Variables de objetos xml
    static private GoogleMap mMap;
    SupportMapFragment mapFragment;
    MaterialButton btnNewEvent;
    MaterialTextView textViewStartEmpty;
    ListView listEvents;
    RelativeLayout item;

    //Variables globales
    ListItemEvents listItemEvents;
    AdapterEvents adapterEvents;
    long date;
    String name;
    String location;
    double latitude;
    double longitude;
    Calendar calendar;
    String json;
    SharedPreferences sharedPreferences;
    MarkerOptions options;
    Marker markerPrevious;
    ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Obtenga SupportMapFragment y reciba una notificación cuando el mapa esté listo para ser utilizado.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewEvent);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Se enlazan los objetos xml
        btnNewEvent = findViewById(R.id.btnNewEvent);
        textViewStartEmpty = findViewById(R.id.textViewStartEmpty);
        listEvents = findViewById(R.id.listEvents);
        item = findViewById(R.id.item);

        //Se inicia las variables
        calendar = Calendar.getInstance();
        sharedPreferences = getSharedPreferences("Events", MODE_PRIVATE);
        json = sharedPreferences.getString("event", "");
        listItemEvents = new ListItemEvents();
        options = new MarkerOptions();
        markers = new ArrayList<>();

        //Se comprueba si el objeto json está vacío.
        if (!json.isEmpty()) {
            listItemEvents = listItemEvents.fromJson(json.intern());
            textViewStartEmpty.setText(R.string.selectEvent);
        }

        //Se inicia el adapter con el objeto a rellenar y el arraylist, se notifica que ha cambiado
        // se agrega al objeto .xml y a este último se le concede un listener para escuchar los clicks
        // sobre sus objetos agregados.
        adapterEvents = new AdapterEvents(this, R.layout.item_map, listItemEvents.allEvents);
        adapterEvents.notifyDataSetChanged();
        listEvents.setAdapter(adapterEvents);
        listEvents.setOnItemClickListener(this);

        //Listener del botón nuevo evento, llama a la nueva actividad con un intent esperando un resultado
        btnNewEvent.setOnClickListener(v -> {
            Intent dateIntent = new Intent(this, DateActivity.class);
            startActivityForResult(dateIntent, 0);
        });

    }

    //Método para capturar el resultado del intent que inicia la actividad desde el botón newEvent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Si el código devuelto es 0 y el resultado de la consulta es OK
        if (requestCode == 0 && resultCode == RESULT_OK) {

            //se carga en date los datos recuperados del activity.
            assert data != null;
            date = data.getLongExtra("date", 0);

            //Ahora se llama a un nuevo activity para solicitar datos al usuario.
            Intent nameIntent = new Intent(this, NameActivity.class);
            startActivityForResult(nameIntent, 1);

        }

        //Una vez que el activity llamado se cierra, si el código devuleto es 1 y el resultado es OK
        if (requestCode == 1 && resultCode == RESULT_OK) {

            //Se capturan los datos obtenidos del activity
            assert data != null;
            name = data.getExtras().getString("name", "EMPTY");
            location = data.getExtras().getString("location", "EMPTY");
            latitude = data.getExtras().getDouble("latitude", 0);
            longitude = data.getExtras().getDouble("longitude", 0);

            //Se crea el marcador con la ubicación obtenida del activity y se desplaza la camara al lugar.
            LatLng latLng = new LatLng(latitude, longitude);
            markers.add(mMap.addMarker(new MarkerOptions()
                    .title(location)
                    .position(latLng)
                    .snippet(name + " " + getString(R.string.in) + " " + location)));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

            //Se llama a newEvent
            newEvent();
            newNotify(name);
            textViewStartEmpty.setText(R.string.selectEvent);

        }
    }

    //Méotod para escuchar los clicks sobre los objetos de la lista.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Se llama a markerSelect para cambiar la visualización del marker seleccionado.
        markerSelect(markers.get(position));

    }

    //Modifica el marker selecionado.
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Se llama a markerSelect para cambiar la visualización del marker seleccionado.
        markerSelect(marker);
        return false;
    }

    //Notifica sobre un nuevo marker añadido.
    public void newNotify(String name){
        String CHANNEL_ID = "MYCHANNEL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nCh = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH);
            //PendingIntent pI = PendingIntent.getActivity(this, 1, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Nuevo Evento: " + name)
                    .setContentText("no se que enseña esto")
                    .setColor(Color.BLUE)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.greenmarker)
                    .build();

            NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.createNotificationChannel(nCh);
            nManager.notify(markers.size(), notification);
        }
    }

    //Método para eliminar eventos
    public void deleteEvent(int position){
        //Se elimina el marker en el mapa
        markers.get(position).remove();
        //Se elimina el marker de la array de los markers.
        markers.remove(position);
        //Se elimina la posición de la lista de eventos
        listItemEvents.allEvents.remove(position);

        //Se actualiza en sharepreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("event", listItemEvents.toJson());
        editor.apply();

        //Se notifica el cambio al adapter.
        adapterEvents.notifyDataSetChanged();
    }

    private void markerSelect(Marker marker) {

        //Si ya se había seleccionado un marker previamente se devuelve este a su imagen original
        //y se oculta su información.
        if (markerPrevious != null) {
            try {
                markerPrevious.setAlpha(0.8f);
                markerPrevious.setIcon(BitmapDescriptorFactory.defaultMarker());
                markerPrevious.hideInfoWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        //Se modifica el color del marker que pertenece al evento seleccionado.
        marker.setAlpha(0.8f);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //Se mueve el mapa a la ubicación elegida y se hace zoom.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

        //Se optiene la info del marker y se prepara.
        String infoEvent = marker.getSnippet();
        infoEvent = infoEvent.replace(",", "\n");

        //Se carga la info en el textView
        textViewStartEmpty.setText(infoEvent);

        //Se muestra la info del marker sobre este.
        marker.showInfoWindow();

        //Se carga este marker para restaurarlo a la imagen original al seleccionar otro.
        markerPrevious = marker;
    }

    //Función para guardar los nuevos eventos.
    private void newEvent() {

        //Añade elnuevo item al array
        listItemEvents.allEvents.add(itemEvent());

        //Se guarda en sharepreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("event", listItemEvents.toJson());
        editor.apply();

        //se actualzia el adapter
        adapterEvents.notifyDataSetChanged();

    }

    //Función para construir el nuevo evento.
    private ItemEvent itemEvent() {
        ItemEvent itemEvent;
        calendar.setTimeInMillis(date);
        itemEvent = new ItemEvent(name
                , calendar.get(Calendar.DAY_OF_WEEK) + "/"
                + solveMonth(calendar.get(Calendar.MONTH)) + "/"
                + calendar.get(Calendar.YEAR) + " " + getString(R.string.att) + " " +
                +calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + solveMinutes(calendar.get(Calendar.MINUTE))
                , location
                , latitude
                , longitude);
        return itemEvent;
    }

    //Función para solventar el problema de la numeración de los meses en el calendario gregoriano.
    static public String solveMonth(int month) {
        String mth;
        month++;
        mth = String.valueOf(month);
        return mth;
    }

    //Función para solventar la numeración de los minutos añadiendo un 0 en caso de unidades.
    static public String solveMinutes(int minuteInt) {
        String minutes;

        if (minuteInt < 10) {
            minutes = "0" + minuteInt;

        } else {
            minutes = String.valueOf(minuteInt);

        }
        return minutes;
    }


    //Se carga el mapa con los eventos, y sus respectivos marker, que estén guardados.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Se revisan los premisos para acceder a la ubicación.
        localitationActivate();

        //se carga una ubicació predefinida para centrar el mapa
        LatLng latLng = new LatLng(38.26218, -0.70107);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Se cargan los eventos y sus marker.
        for (int i = 0; i < listItemEvents.allEvents.size(); i++) {
            location = listItemEvents.allEvents.get(i).getLocation();
            name = listItemEvents.allEvents.get(i).getName();
            String dateStirng = listItemEvents.allEvents.get(i).getDate();

            LatLng site = new LatLng(listItemEvents.allEvents.get(i).getLatPoint(), listItemEvents.allEvents.get(i).getLngPoint());
            markers.add(mMap.addMarker(new MarkerOptions()
                    .alpha(0.8f)
                    .title(location)
                    .position(site)
                    .snippet(name + ", el " + dateStirng)));

        }

        mMap.setOnMarkerClickListener(this);
    }

    //Se comprueba que haya permiso para usar el gps
    public void localitationActivate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;

        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            localitationActivate();

        } else {
            Toast.makeText(this, "Si no activas la localización puede que la app no funcione correctamente.", Toast.LENGTH_LONG).show();
        }
    }


}
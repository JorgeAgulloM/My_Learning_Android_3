package com.example.eventpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.io.IOException;
import java.util.List;

public class NameActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Activity para que el usuario introduzca el nombre del evento y su ubicación. Esta se verifica
    //antes de pasarla.

    //Variables para los objetos xml.
    static private GoogleMap mMap;
    SupportMapFragment mapFragment;
    List<Address> addressList;
    View view;
    MaterialAutoCompleteTextView txtFieldName;
    MaterialAutoCompleteTextView txtFieldLocation;
    MaterialButton btnViewSearch;
    MaterialButton btnAddNewEvent;

    //Atributos
    String name;
    String location;
    LatLng latlng;
    NameActivity nameActivity;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        //Se modifica el nombre de la barra de UI.
        setTitle(getString(R.string.nameAndPlace));

        //Se carga el fragment en el que se muestra el mapa.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewEvent);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //Se enlazan los objetos xml
        view = findViewById(R.id.activity_name);
        txtFieldName = findViewById(R.id.txtFieldName);
        txtFieldLocation = findViewById(R.id.txtFieldSite);
        btnViewSearch = findViewById(R.id.btnViewSearch);
        btnAddNewEvent = findViewById(R.id.btnAddNewEvent);

        //Se inician las variables.
        nameActivity = this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Listener para escuchar el click del botón search que verifica la dirección propuesta
        //por el usuario.
        btnViewSearch.setOnClickListener(v -> {

            //Se definen y carga las variables necesarias.
            String searching = txtFieldLocation.getText().toString();
            inputMethodManager.hideSoftInputFromWindow(btnViewSearch.getWindowToken(), 0);

            //Se verifica que haya texto en el editText
            if (searching.equals("")) {
                //Si no hay se informa al usuario.
                txtFieldLocation.setError(getString(R.string.needAddLocation));
                Toast.makeText(nameActivity, R.string.noDirection, Toast.LENGTH_SHORT).show();
            } else {

                //Secarga el texto de la localización
                location = txtFieldLocation.getText().toString();

                //Se crea el objeto Geocoder para conocer el lugar indicado.
                Geocoder geocoder = new Geocoder(getApplicationContext());

                try {
                    //Se comprueba el objeto para obtener su posición en el globo. Se buscan máximo 3 resultados
                    addressList = geocoder.getFromLocationName(location, 3);
                    if (addressList != null) { //Si hay algo en el array

                        //Se inicia un bucle para extraer los resultados y mostrarlos en le mapa
                        //mediante markers.
                        for (int i = 0; i < addressList.size(); i++) {
                            Address address = addressList.get(i);
                            latlng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions()
                                    .title(address.getLocality())
                                    .position(latlng));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(11));

                        }

                        //Se permite el uso del botón newEvent
                        btnAddNewEvent.setEnabled(true);
                    } else if (addressList.size() == 0) {
                        //Si no hay busqueda se infroma al usuario
                        Toast.makeText(nameActivity, R.string.errorNothing, Toast.LENGTH_SHORT).show();
                    }

                    //En caso de error se infroma al usuario.
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(nameActivity, R.string.errorTryAnother, Toast.LENGTH_SHORT).show();
                } catch (Exception a) {
                    Toast.makeText(nameActivity, R.string.errorTryAnother, Toast.LENGTH_SHORT).show();
                }
            }


        });

        //Listener para el botón NewEvent para terminar el activity pasando los datos al principal
        btnAddNewEvent.setOnClickListener(v -> {

            //Se comprueba si hay texto
            if (txtFieldName != null && !txtFieldName.getText().toString().equals("")) {
                //Se comprueba si hay texto
                if (txtFieldLocation != null && !txtFieldLocation.getText().toString().equals("")) {

                    name = txtFieldName.getText().toString();
                    location = txtFieldLocation.getText().toString();

                    Intent newIntent = new Intent(this, MainMapsActivity.class);
                    newIntent.putExtra("name", name);
                    newIntent.putExtra("location", location);
                    newIntent.putExtra("latitude", latlng.latitude);
                    newIntent.putExtra("longitude", latlng.longitude);
                    setResult(RESULT_OK, newIntent);
                    finish();

                    //Se infarma al usuario si no hay datos en algún editText.
                } else {
                    assert txtFieldLocation != null;
                    txtFieldLocation.setError(getString(R.string.needAddLocation));
                    snackBarCreate(getString(R.string.whiteNewPlaces));
                }

            } else {
                assert txtFieldName != null;
                txtFieldName.setError(getString(R.string.needAddName));
                snackBarCreate(getString(R.string.writeNewName));
            }

        });


    }

    //Función para lanzar el snackBar.
    public void snackBarCreate(String text) {

        Snackbar.make(view, text, BaseTransientBottomBar.LENGTH_LONG).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
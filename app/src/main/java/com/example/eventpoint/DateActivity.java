package com.example.eventpoint;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;

import static com.example.eventpoint.MainMapsActivity.solveMinutes;
import static com.example.eventpoint.MainMapsActivity.solveMonth;

public class DateActivity extends AppCompatActivity {

    //Variables para enlazar a objetos xml
    DatePicker viewDatePicker;
    TimePicker viewTimePicker;
    MaterialButton btnNewTime;
    MaterialTextView textViewResult;
    MaterialButton btnViewContinue;
    MaterialButton btnViewCancel;

    //Atributos
    Calendar calendar1;
    Calendar calendar2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        //Se adapta el títutlo de la barra UI
        setTitle(getString(R.string.dateHour));

        //Se enlazan los objetos xml
        viewDatePicker = findViewById(R.id.viewDatePicker);
        viewTimePicker = findViewById(R.id.viewTimePicker);
        btnNewTime = findViewById(R.id.btnNewTime);
        textViewResult = findViewById(R.id.textViewResult);
        btnViewContinue = findViewById(R.id.btnViewContinue);
        btnViewCancel = findViewById(R.id.btnViewCancel);

        //Se inicia calendar1, se obtiene su valor en milisegundos desde el inicio de los tiempos
        //y se pasa este valor al datePicker para evitar el uso de dias anteriores al de hoy.
        calendar1 = Calendar.getInstance();
        long minDate = calendar1.getTimeInMillis();
        viewDatePicker.setMinDate(minDate);

        //Se ecucha el click sobre el día seleccionado del datePicker y se obtiene su valor.
        //Se ocultan y muestran objetos visuales.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            viewDatePicker.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                calendar1.set(year, monthOfYear, dayOfMonth);
                viewDatePicker.setVisibility(View.INVISIBLE);
                viewTimePicker.setVisibility(View.VISIBLE);
                btnNewTime.setVisibility(View.VISIBLE);
            });
        }

        //Se inicia calendar2
        calendar2 = Calendar.getInstance();

        //Se escucla el click sobre el botón de aceptar del timePicker para guardar los valores
        //seleccionados y añadirlos a calendar2 junto con los de calendar1.
        btnNewTime.setOnClickListener(v -> {
            calendar2.set(calendar1.get(Calendar.YEAR)
                    , calendar1.get(Calendar.MONTH)
                    , calendar1.get(Calendar.DAY_OF_MONTH)
                    , viewTimePicker.getHour()
                    , viewTimePicker.getMinute());

            //Se reinicia calendar1 para usarlo de referencia del momento actual.
            calendar1 = Calendar.getInstance();

            //Se obtienen los valores de calendar1 y 2 en milisegundos desde el origen.
            long now = calendar1.getTimeInMillis();
            long newTime = calendar2.getTimeInMillis();

            //Se comparan los valores de tiempo.
            if (newTime < now) { //Si el nuevo valor de tiempo es menor que el actual.
                //Se anuncia al usuario de que no puede usar una hora o un dia anteriores al actual.
                Toast.makeText(this, R.string.notSelect, Toast.LENGTH_LONG).show();

            } else { //Si el valor escorrecto.
                //Se muestra la fecha y hora selecionadas por el usuario.
                textViewResult.setText(getString(R.string.dateIs)
                        + calendar2.get(Calendar.DAY_OF_MONTH)
                        + "/" + solveMonth(calendar2.get(Calendar.MONTH))
                        + "/" + calendar2.get(Calendar.YEAR)
                        + " " + getString(R.string.att) + " " + calendar2.get(Calendar.HOUR)
                        + ":" + solveMinutes(calendar2.get(Calendar.MINUTE)));

                //Se ocultan y activan objetos xml para interacuar con el usuario.
                textViewResult.setVisibility(View.VISIBLE);
                viewTimePicker.setVisibility(View.INVISIBLE);
                btnNewTime.setVisibility(View.INVISIBLE);
                btnViewContinue.setEnabled(true);
                btnViewCancel.setEnabled(true);
            }
        });

        //Se escucha el click sobre el botón continuar para devolver los valores al activity principal
        //con el intent y se finaliza este activity.
        btnViewContinue.setOnClickListener(v -> {

            long date = calendar2.getTimeInMillis();

            Intent activityIntet = getIntent();
            activityIntet.putExtra("date", date);
            setResult(RESULT_OK, activityIntet);
            finish();
        });

        //Se escucha el botón cancelar. En caso de que el usuario quiera cambiar la fecha, se reinicia
        //el estado visual del activity.
        btnViewCancel.setOnClickListener(v -> {
            btnViewContinue.setEnabled(false);
            btnViewCancel.setEnabled(false);
            textViewResult.setVisibility(View.INVISIBLE);
            viewDatePicker.setVisibility(View.VISIBLE);
        });

    }
}
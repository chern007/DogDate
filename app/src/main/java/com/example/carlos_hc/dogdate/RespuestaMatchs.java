package com.example.carlos_hc.dogdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RespuestaMatchs extends AppCompatActivity {

    EditText tableroMensajes;
    String claveMiPerro;
    String nombreMiPerro;
    String claveOtroPerro;
    String nombreOtroPerro;

    TreeMap<Calendar,String> TODOSlosMENSAJES;

    //para gestionar los eventos de escucha de la base de datos
    DatabaseReference listenerMensajesMiperro;
    ValueEventListener eventoEscuchaMensajes;

    EditText txtMiMensaje;

    boolean fechaPreparada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_matchs);



        txtMiMensaje = findViewById(R.id.txtMiMensaje);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  Mensajes");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //iniciamos el diccionario con todos los mensajes
        TODOSlosMENSAJES = new TreeMap<>();

        tableroMensajes = findViewById(R.id.txtTableroMensajes);

        //obtenemos los identificadores de los perros que van a intervenir en la conversacion
        claveMiPerro = getIntent().getStringExtra("claveMiPerro");
        claveOtroPerro = getIntent().getStringExtra("claveOtroPerro");
        nombreMiPerro = getIntent().getStringExtra("nombreMiPerro");
        nombreOtroPerro = getIntent().getStringExtra("nombreOtroPerro");


        listenerMensajesMiperro = FirebaseDatabase.getInstance().getReference("matches").child(claveMiPerro);

        eventoEscuchaMensajes = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    fechaPreparada = true;

                    //limpiamos la lista de mensajes
                    TODOSlosMENSAJES.clear();

                    for (DataSnapshot snapShot : dataSnapshot.getChildren()) {


                        String mensaje = snapShot.child("contenido").getValue().toString();

                        for ( DataSnapshot tusa : snapShot.getChildren()) {

                            Log.i("--->",tusa.getValue().toString() );
                        }

                        //vemos si todos los mensajes tienen fecha asignada
                        if(snapShot.child("fecha").getValue() != null){

                            String preFecha = snapShot.child("fecha").getValue().toString();

                            String[] fecha_hora = preFecha.split(" ");

                            String[] dia_mes_año = fecha_hora[0].split("/");

                            String[] hora_minutos_segundos = fecha_hora[1].split(":");

                            Calendar fecha = Calendar.getInstance();

                            fecha.set(Integer.parseInt(dia_mes_año[2]), Integer.parseInt(dia_mes_año[1]), Integer.parseInt(dia_mes_año[0]), Integer.parseInt(hora_minutos_segundos[0]), Integer.parseInt(hora_minutos_segundos[1]), Integer.parseInt(hora_minutos_segundos[2]));

                            //añadimos el mensaje a la lista
                            TODOSlosMENSAJES.put(fecha,  nombreOtroPerro + ":\n" + mensaje);
                        }else{

                            fechaPreparada = false;

                        }


                    }
                }


                if(fechaPreparada) {
                    //EXAMINAMOS AHORA SI EL OTRO PERRO TIENE MENSAJES NUESTROS

                    FirebaseDatabase.getInstance().getReference("matches").child(claveOtroPerro).child(claveMiPerro).child("mensajes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {


                                for (DataSnapshot snapShot : dataSnapshot.getChildren()) {


                                    String mensaje = snapShot.child("contenido").getValue().toString();

                                    String preFecha = snapShot.child("fecha").getValue().toString();

                                    String[] fecha_hora = preFecha.split(" ");

                                    String[] dia_mes_año = fecha_hora[0].split("/");

                                    String[] hora_minutos_segundos = fecha_hora[1].split(":");

                                    Calendar fecha = Calendar.getInstance();

                                    fecha.set(Integer.parseInt(dia_mes_año[2]), Integer.parseInt(dia_mes_año[1]), Integer.parseInt(dia_mes_año[0]), Integer.parseInt(hora_minutos_segundos[0]), Integer.parseInt(hora_minutos_segundos[1]), Integer.parseInt(hora_minutos_segundos[2]));

                                    //añadimos el mensaje a la lista
                                    TODOSlosMENSAJES.put(fecha, nombreMiPerro + ":\n" + mensaje);

                                    Log.i("tusa", "se ha cargado el diccionario con todos los mensajes");
                                }
                            }

                            //creamos Spannables para pintar lo que vamos a escribir

                            SpannableStringBuilder builder = new SpannableStringBuilder();


                            String perroHablando = "";

                            String parrafoMensajes = "";

                            HashMap<String, Integer> nombreColor = new HashMap<String, Integer>();
                            nombreColor.put(nombreMiPerro, Color.rgb(51, 153, 51));
                            nombreColor.put(nombreOtroPerro, Color.rgb(0, 153, 153));

                            for (Map.Entry<Calendar, String> mensaje : TODOSlosMENSAJES.entrySet()) {

                                if (perroHablando.equals("")) {

                                    perroHablando = mensaje.getValue().split(":\n")[0];

                                    //parrafoMensajes += mensaje.getValue();

                                    SpannableString lineas = new SpannableString(mensaje.getValue());
                                    lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length(), 0);
                                    builder.append(lineas);


                                } else {

                                    if (mensaje.getValue().startsWith(perroHablando)) {

                                        //parrafoMensajes += "\n" + mensaje.getValue();

                                        SpannableString lineas = new SpannableString("\n\n" + mensaje.getValue());
                                        lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length() + 2, 0);
                                        builder.append(lineas);

                                    } else {

                                        perroHablando = mensaje.getValue().split(":\n")[0];

                                        SpannableString lineas = new SpannableString("\n\n" + mensaje.getValue());
                                        lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length() + 2, 0);
                                        builder.append(lineas);

                                        //parrafoMensajes += "\n" + mensaje.getValue();

                                    }

                                }
                            }

                            //tableroMensajes.getText().clear();
                            tableroMensajes.setText(builder, TextView.BufferType.SPANNABLE);
                            //nos vamos a la ultima linea
                            tableroMensajes.setSelection(tableroMensajes.getText().length());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").addValueEventListener(eventoEscuchaMensajes);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //volvemos a la actividad anterior
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent actividadListaMatchs = new Intent(getApplicationContext(),ListaMatchs.class);

                actividadListaMatchs.putExtra("miPerroKey", claveMiPerro);
                actividadListaMatchs.putExtra("miPerroNombre", nombreMiPerro);

                startActivity(actividadListaMatchs);
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").removeEventListener(eventoEscuchaMensajes);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").removeEventListener(eventoEscuchaMensajes);
    }

    @Override
    protected void onStart() {
        super.onStart();

        listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").addValueEventListener(eventoEscuchaMensajes);
    }

    public void enviarMensaje(View view){

        String mensaje = txtMiMensaje.getText().toString();

        //sacamos la fecha

        Calendar ahoraCal = Calendar.getInstance();
        int dia = ahoraCal.get(Calendar.DAY_OF_MONTH);
        int mes = ahoraCal.get(Calendar.MONTH) + 1;
        int año = ahoraCal.get(Calendar.YEAR);
        int hora =ahoraCal.get(Calendar.HOUR_OF_DAY);
        int minutos = ahoraCal.get(Calendar.MINUTE);
        int segundos =  ahoraCal.get(Calendar.SECOND);

        String fecha = dia +"/"+ mes +"/"+ año +" "+hora +":"+ minutos +":"+ segundos;

        DatabaseReference nodoMensaje = FirebaseDatabase.getInstance().getReference("matches").child(claveOtroPerro).child(claveMiPerro).child("mensajes").push();
        nodoMensaje.child("contenido").setValue(mensaje);
        nodoMensaje.child("fecha").setValue(fecha);

        listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").addListenerForSingleValueEvent(eventoEscuchaMensajes);

        //limpiamos el mensaje que acabamos de enviar
        txtMiMensaje.getText().clear();

        hideKeyboard(RespuestaMatchs.this);

    }

    //Metodo que oculta el teclado si esta activo
    @NonNull
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}

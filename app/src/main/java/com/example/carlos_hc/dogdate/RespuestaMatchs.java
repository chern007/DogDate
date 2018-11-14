package com.example.carlos_hc.dogdate;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RespuestaMatchs extends AppCompatActivity {

    EditText tableroMensajes;
    String claveMiPerro;
    String nombreMiPerro;
    String claveOtroPerro;
    String nombreOtroPerro;

    TreeMap<Calendar,String> TODOSlosMENSAJES;

    DatabaseReference listenerMensajesMiperro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_matchs);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  Mensajes");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //iniciamos el diccionario con todos los mensajes
        TODOSlosMENSAJES = new TreeMap<>();

        tableroMensajes = findViewById(R.id.txtTableroMensajes);

        //obtenemos los identificadores de los perros que van a intervenir en la conversacion
        claveMiPerro = getIntent().getStringExtra("claveMiPerro");
        claveOtroPerro = getIntent().getStringExtra("claveOtroPerro");
        nombreMiPerro = getIntent().getStringExtra("nombreMiPerro");
        nombreOtroPerro = getIntent().getStringExtra("nombreOtroPerro");


        listenerMensajesMiperro = FirebaseDatabase.getInstance().getReference("matches").child(claveMiPerro);

        listenerMensajesMiperro.child(claveOtroPerro).child("mensajes").addListenerForSingleValueEvent(new ValueEventListener() {
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
                        TODOSlosMENSAJES.put(fecha,  nombreOtroPerro + ":\n" + mensaje);
                    }
                }

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

                                Log.i("tusa","se ha cargado el diccionario con todos los mensajes");
                            }
                        }

                        //creamos Spannables para pintar lo que vamos a escribir

                        SpannableStringBuilder builder = new SpannableStringBuilder();





                        String perroHablando = "";

                        String parrafoMensajes = "";


                        HashMap<String,Integer> nombreColor = new HashMap<String,Integer>();
                        nombreColor.put(nombreMiPerro,Color.BLACK);
                        nombreColor.put(nombreOtroPerro,Color.RED);

                        for ( Map.Entry<Calendar,String> mensaje : TODOSlosMENSAJES.entrySet()) {

                            if (perroHablando.equals("") ){

                                perroHablando = mensaje.getValue().split(":\n")[0];

                                //parrafoMensajes += mensaje.getValue();

                                SpannableString lineas = new SpannableString(mensaje.getValue());
                                lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length(), 0);
                                builder.append(lineas);


                            }else{

                                if (mensaje.getValue().startsWith(perroHablando)) {

                                    //parrafoMensajes += "\n" + mensaje.getValue();

                                    SpannableString lineas = new SpannableString("\n\n" + mensaje.getValue());
                                    lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length() + 2, 0);
                                    builder.append(lineas);

                                }else{

                                    perroHablando = mensaje.getValue().split(":\n")[0];

                                    SpannableString lineas = new SpannableString("\n\n" + mensaje.getValue());
                                    lineas.setSpan(new ForegroundColorSpan(nombreColor.get(perroHablando)), 0, mensaje.getValue().length() + 2, 0);
                                    builder.append(lineas);

                                    //parrafoMensajes += "\n" + mensaje.getValue();

                                }

                            }
                        }

                        tableroMensajes.setText(builder, TextView.BufferType.SPANNABLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });









            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();

        //listenerMensajesMiperro.removeEventListener();

    }
}

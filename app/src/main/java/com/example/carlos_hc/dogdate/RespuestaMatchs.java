package com.example.carlos_hc.dogdate;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TreeMap;

public class RespuestaMatchs extends AppCompatActivity {

    EditText tableroMensajes;
    String claveMiPerro;
    String nombreMiPerro;
    String claveOtroPerro;
    String nombreOtroPerro;

    TreeMap<Calendar,String> TODOSlosMENSAJES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_matchs);

        //iniciamos el diccionario con todos los mensajes
        TODOSlosMENSAJES = new TreeMap<>();

        tableroMensajes = findViewById(R.id.txtTableroMensajes);

        //obtenemos los identificadores de los perros que van a intervenir en la conversacion
        claveMiPerro = getIntent().getStringExtra("claveMiPerro");
        claveOtroPerro = getIntent().getStringExtra("claveOtroPerro");
        nombreMiPerro = getIntent().getStringExtra("nombreMiPerro");
        nombreOtroPerro = getIntent().getStringExtra("nombreOtroPerro");

        FirebaseDatabase.getInstance().getReference("matches").child(claveMiPerro).child(claveOtroPerro).child("mensajes").addListenerForSingleValueEvent(new ValueEventListener() {
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
}

package com.example.carlos_hc.dogdate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import Objetos.Perro;
import Objetos.PerroAdapter;

public class ListaMatchs extends AppCompatActivity {

    private List<Perro> perrosListaMatchs = new ArrayList<>();

    private RecyclerView recyclerView;
    private PerroAdapter perroAdapter;
    public Context contextoMatchs;
    String miPerroKey;
    String nombreMiPerro;

    List<String> listaDeKeysOrdenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_matchs);

        //iniciamos la lista de keys
        listaDeKeysOrdenadas = new ArrayList<>();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  Matchs");
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        //obtenemos el contexto de la aplicacion
        contextoMatchs = getApplicationContext();

        //obtenemos la key de nuestro perro
        miPerroKey = getIntent().getStringExtra("miPerroKey");
        //obtenemos el nomnbre de nuestro perro
        nombreMiPerro = getIntent().getStringExtra("miPerroNombre");

        recyclerView = findViewById(R.id.lstMatchs);

        perroAdapter = new PerroAdapter(perrosListaMatchs, contextoMatchs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(perroAdapter);

        recyclerView.addOnItemTouchListener(new ListaMatchsOnClickListener(getApplicationContext(), recyclerView, new ListaMatchsOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Toast.makeText(getApplicationContext(), position + " is selected!", Toast.LENGTH_SHORT).show();

                Perro itemPerroSeleccionado = perrosListaMatchs.get(position);

                //iniciamos la actividad para ver mi perfil
                Intent actividadPanelMensajes = new Intent(getApplicationContext(),RespuestaMatchs.class);
                actividadPanelMensajes.putExtra("claveMiPerro", miPerroKey);
                actividadPanelMensajes.putExtra("claveOtroPerro", listaDeKeysOrdenadas.get(position));
                actividadPanelMensajes.putExtra("nombreMiPerro", nombreMiPerro);
                actividadPanelMensajes.putExtra("nombreOtroPerro", itemPerroSeleccionado.getNombre());
                startActivity(actividadPanelMensajes);

                Log.i("INFO: ", "Hasta aqui Ok");

            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));

        prepararPerros();

    }

    private void prepararPerros() {

        //obtenemos los matches
        FirebaseDatabase.getInstance().getReference("matches").child(miPerroKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //recorremos todos los match
                if (dataSnapshot.exists()) {


                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String clave = snapshot.getKey();
                        listaDeKeysOrdenadas.add(clave);//añadimmos la clave a la lista de keys

                        FirebaseDatabase.getInstance().getReference("usuarios").child(clave).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                if (dataSnapshot2.exists()) {

                                    //añadimos el perro a nuestra lista de perros
                                    Perro tmp = dataSnapshot2.getValue(Perro.class);
                                    perrosListaMatchs.add(tmp);

                                    //hay que hacer la notificacion al adapter dentro porque la obtencion de los elementos corre en un hilo distinto
                                    //y cuando actualiza la lista aun no ha obtenido los elem de la base de datos y no pinta nada en ella
                                    perroAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }





}

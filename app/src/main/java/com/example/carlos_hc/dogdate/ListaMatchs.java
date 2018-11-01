package com.example.carlos_hc.dogdate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    private List<Perro> perrosListaMatchs = new ArrayList<>();;
    private RecyclerView recyclerView;
    private PerroAdapter perroAdapter;
    public Context contextoMatchs;
    String miPerroKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_matchs);

        //obtenemos el contexto de la aplicacion
        contextoMatchs = getApplicationContext();

        //obtenemos la key de nuestro perro
        miPerroKey = getIntent().getStringExtra("miPerroKey");

        recyclerView = findViewById(R.id.lstMatchs);

        perroAdapter = new PerroAdapter(perrosListaMatchs, contextoMatchs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(perroAdapter);

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
                        String mensaje = (String) snapshot.getValue();


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

                    //perroAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });


//        Perro ringo = new Perro();
//        ringo.setNombre("ringo");
//        ringo.setRaza("pinscher");
//        ringo.setEmail("ringo@gmail.com");
//        ringo.setGenero("macho");
//        perrosListaMatchs.add(ringo);
//
//        Perro nala = new Perro();
//        nala.setNombre("nala");
//        nala.setRaza("yorkshike");
//        nala.setEmail("nala@gmail.com");
//        nala.setGenero("hembra");
//        perrosListaMatchs.add(nala);


        //perroAdapter.notifyDataSetChanged();

    }


//    private void tusa(String clave){
//
//        FirebaseDatabase.getInstance().getReference("usuarios").child(clave).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
//
//                if (dataSnapshot2.exists()) {
//
//                        //añadimos el perro a nuestra lista de perros
//                        perrosListaMatchs.add(dataSnapshot2.getValue(Perro.class));
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }


}

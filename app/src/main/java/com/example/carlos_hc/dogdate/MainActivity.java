package com.example.carlos_hc.dogdate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Objetos.Perro;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    //creamos una lista de perros donde guardaremos los resultados de las queries
    List<Perro> perros = new ArrayList<>();
    Perro miPerro;

    Context miCOntexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miCOntexto = getApplicationContext();


        //CONECTAMOS A LA tabla "usuarios"
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

//        //COMO CREAR UN NUEVO MIEMBRO EN LA BASE DE DATOS
//        Perro churrete = new Perro(3,"churrete@gmail.com","macho","churrete", "chucho");
//        mDatabase.child("47").setValue(churrete);

//        //ACTUALIZAR UNA PROPIEDAD DE UN MIEMBRO
//        mDatabase.child("46").child("genero").setValue("Macho");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                miPerro = dataSnapshot.child("1").getValue(Perro.class);

                Log.i("TUSA",miPerro.getNombre());

                Toast.makeText(miCOntexto,miPerro.getNombre(),Toast.LENGTH_LONG);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabase.addValueEventListener(postListener);



        ///@@@@_QUERIES_@@@@
        //Objeto que saca todos los hijos de la "BBDD" a la que apunta mDatabase
//    ValueEventListener valueEventListener = new ValueEventListener()  {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//
//            perros.clear();
//
//
//            if (dataSnapshot.exists()){
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Perro tmp = snapshot.getValue(Perro.class);
//                    perros.add(tmp);
//                }
//            }
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError)  {
//
//        }
//    };






    }



}

package com.example.carlos_hc.dogdate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    String emailLogin;
    Perro miPerro;
    String miPerroKey;

    Context miCOntexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtenemos el email del logueo
        emailLogin = getIntent().getStringExtra("email");

        //obtenemos el contexto por si lo necesitamos mas adelante
        miCOntexto = getApplicationContext();


        //CONECTAMOS A LA tabla "usuarios"
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

        //obtenemos los datos de nuestro perro por medio de un objeto Perro
        Query queryMiPerro = mDatabase.orderByChild("email").equalTo(emailLogin);
        queryMiPerro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //nos devuelve ina coleccion de resulatados
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        miPerro = snapshot.getValue(Perro.class);
                        miPerroKey = snapshot.getKey();
                    }
                }


                Log.i("TUSA", String.valueOf(perros.size()));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        //COMO CREAR UN NUEVO MIEMBRO EN LA BASE DE DATOS
//        Perro churrete = new Perro(3,"churrete@gmail.com","macho","churrete", "chucho");
//        mDatabase.child("47").setValue(churrete);

//        //ACTUALIZAR UNA PROPIEDAD DE UN MIEMBRO
//        mDatabase.child("46").child("genero").setValue("Macho");

//        //nos saca los datos de un solo perro
//        ValueEventListener postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                miPerro = dataSnapshot.child("1").getValue(Perro.class);
//
//                Log.i("TUSA", miPerro.getNombre());
//
//                Toast.makeText(miCOntexto, miPerro.getNombre(), Toast.LENGTH_LONG);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };


        ///@@@@_QUERIES_@@@@
        //Objeto que saca todos los hijos de la "BBDD" a la que apunta mDatabase
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                perros.clear();
//
////                if (dataSnapshot.exists()) {
////                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
////                        Perro tmp = snapshot.getValue(Perro.class);
////                        perros.add(tmp);
////                    }
////                }
//
//                Log.i("TUSA", String.valueOf(perros.size()));
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        mDatabase.addValueEventListener(valueEventListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(), "Item 2 Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

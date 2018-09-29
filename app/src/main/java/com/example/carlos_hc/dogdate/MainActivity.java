package com.example.carlos_hc.dogdate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Objetos.Perro;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //COMO ESCRIBIR EN FIREBASE BBDD
        mDatabase = FirebaseDatabase.getInstance().getReference("usuarios");

        Perro churrete = new Perro(3,"churrete@gmail.com","macho","churrete", "chucho");


        mDatabase.child("46").setValue(churrete);


        Log.i("INFORMACION","Se ha hecho un add a la base de datos");


    }
}

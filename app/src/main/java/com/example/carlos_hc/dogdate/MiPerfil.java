package com.example.carlos_hc.dogdate;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Objetos.Perro;

public class MiPerfil extends AppCompatActivity {

    Perro miPerro;
    ImageView imagenPerro;
    TextView txtNombre;
    TextView txtRaza;
    TextView txtGenero;
    TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  Mi Perfil");
        getSupportActionBar().setDisplayUseLogoEnabled(true);



        String miPerroKey = getIntent().getStringExtra("miPerroKey");

        imagenPerro = findViewById(R.id.imgPerro2);
        txtNombre = findViewById(R.id.txtNombre2);
        txtGenero = findViewById(R.id.txtGenero2);
        txtEmail = findViewById(R.id.txtEmail2);
        txtRaza = findViewById(R.id.txtRaza2);


        //sacamos los usuarios - los match y los discarts (son los que se presentaran en las cartas de presentación)
        FirebaseDatabase.getInstance().getReference("usuarios").child(miPerroKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    miPerro = dataSnapshot.getValue(Perro.class);

                    //cargamos la foto del primer perro
                    cargarFotoPorEmail(miPerro.getEmail());
                    txtEmail.setText("Email: " + miPerro.getEmail());
                    txtGenero.setText("Género: " + miPerro.getGenero());
                    txtNombre.setText("Nombre: " + miPerro.getNombre());
                    txtRaza.setText("Raza: " + miPerro.getRaza());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void cargarFotoPorEmail(String email) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("dogDate/" + email + ".jpg");

        // Load the image using Glide
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .into(imagenPerro);


    }


}

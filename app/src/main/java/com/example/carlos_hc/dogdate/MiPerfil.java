package com.example.carlos_hc.dogdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    DatabaseReference infoMiPerro;
    ValueEventListener hayCambiosMiPerro;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        infoMiPerro = FirebaseDatabase.getInstance().getReference("usuarios").child(miPerroKey);

        //sacamos los usuarios - los match y los discarts (son los que se presentaran en las cartas de presentación)
        hayCambiosMiPerro = new ValueEventListener() {
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
        };

        infoMiPerro.addListenerForSingleValueEvent(hayCambiosMiPerro);


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        infoMiPerro.addListenerForSingleValueEvent(hayCambiosMiPerro);
//
//    }


    private void cargarFotoPorEmail(String email) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10);
        circularProgressDrawable.setCenterRadius(50);
        circularProgressDrawable.start();


        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("dogDate/" + email + ".jpg");

        // Load the image using Glide
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(circularProgressDrawable)
                .into(imagenPerro);


    }

    private static int FOTO_GUARDADA = 1;

    public void editarPerfil(View view) {


        //iniciamos la actividad para ver mi perfil
        Intent actividadEditarPerfil = new Intent(getApplicationContext(), EditarPerfil.class);
        actividadEditarPerfil.putExtra("miPerroEmail", miPerro.getEmail());
        startActivityForResult(actividadEditarPerfil,FOTO_GUARDADA);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FOTO_GUARDADA) {
            if(resultCode == Activity.RESULT_OK){
                String result =data.getStringExtra("result");

                cargarFotoPorEmail(result);
                infoMiPerro.addListenerForSingleValueEvent(hayCambiosMiPerro);//volvemos a consultar los datos de nuestro perro
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult




    public static Bitmap bitmapFotoActual = null;

    public static void obtenerBitmapFotoActual(String miPerroEmail) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("dogDate");

        StorageReference islandRef = storageRef.child(miPerroEmail + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

                bitmapFotoActual = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

    }



}

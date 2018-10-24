package com.example.carlos_hc.dogdate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Objetos.Perro;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    //creamos una lista de perros donde guardaremos los resultados de las queries
    List<Perro> misPerros;//lista de perros para ver
    List<Perro> misPerrosVistos;//lista de perros que hemos visto
    String emailLogin;
    Perro miPerro;
    String miPerroKey;
    Map<String, Object> matches;
    Map<String, Object> discarts;

    Context miCOntexto;
    ImageView imagenPerro;
    TextView txtNombre;
    TextView txtRaza;
    TextView txtGenero;
    TextView txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  DogDate");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        txtNombre = findViewById(R.id.txtNombre);
        txtGenero = findViewById(R.id.txtGenero);
        txtEmail = findViewById(R.id.txtEmail);
        txtRaza = findViewById(R.id.txtRaza);

        //conectamos con la imagen
        imagenPerro = findViewById(R.id.imgPerro);

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

                //nos devuelve una coleccion de resulatados
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        miPerro = snapshot.getValue(Perro.class);
                        miPerroKey = snapshot.getKey();
                    }

                    //sacamos los matches del usuario

                    FirebaseDatabase.getInstance().getReference("matches").child(miPerroKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                matches = new LinkedHashMap<>();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    matches.put(snapshot.getKey(), snapshot.getValue());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //sacamos los discarts del usuario
                    FirebaseDatabase.getInstance().getReference("discarts").child(miPerroKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                discarts = new LinkedHashMap<>();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    discarts.put(snapshot.getKey(), snapshot.getValue());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //sacamos los usuarios - los match y los discarts (son los que se presentaran en las cartas de presentación)
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()) {

                                misPerros = new ArrayList<Perro>();

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    String key = snapshot.getKey();

                                    if (!matches.containsKey(key) && !discarts.containsKey(key) && !key.equals(miPerroKey)) {

                                        misPerros.add(snapshot.getValue(Perro.class));
                                    }
                                }

                                //seguir por aqui
                                Log.i("TUSA", String.valueOf(misPerros.size()));


                                //cargamos la info del primer perro de los perros pendientes de catalogar

                                Perro perroInicio = misPerros.get(0);

                                //cargamos la foto del primer perro
                                cargarFotoPorEmail(perroInicio.getEmail());

                                txtEmail.setText("Email: " + perroInicio.getEmail());
                                txtGenero.setText("Género: " + perroInicio.getGenero());
                                txtNombre.setText("Nombre: " + perroInicio.getNombre());
                                txtRaza.setText("Raza: " + perroInicio.getRaza());

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

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

    //metodo para controlar las acciones de los botones
    public void clickBotones(View view) {

        int idBoton = view.getId();


        switch (idBoton) {

            case R.id.btSiguiente:


                break;


            case R.id.btMatch:


                break;


            case R.id.btDiscart:


                break;


        }


    }


}

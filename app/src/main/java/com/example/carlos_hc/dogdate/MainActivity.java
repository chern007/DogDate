package com.example.carlos_hc.dogdate;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.util.LinkedHashMap;
import java.util.Map;

import Objetos.Perro;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mDatabase;

    //creamos una lista de perros donde guardaremos los resultados de las queries
    Map<String, Object> misPerros;//lista de perros para ver
    Map<String, Object> misPerrosVistos;//lista de perros que hemos visto
    String emailLogin;
    Perro miPerro;
    String miPerroKey;
    Map<String, Object> matches;
    Map<String, Object> no_load;

    Context miCOntexto;
    ImageView imagenPerro;
    TextView txtNombre;
    TextView txtRaza;
    TextView txtGenero;
    TextView txtEmail;


    AlertDialog.Builder builder;
    Map.Entry<String, Object> primerPerro;
    String keyDelPerroActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  DogDate");
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        //iniciamos la lista de perros
        misPerros = new LinkedHashMap<>();
        misPerrosVistos = new LinkedHashMap<>();

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


                    //sacamos los discarts del usuario
                    Query queryDiscarts = FirebaseDatabase.getInstance().getReference("no_load").child(miPerroKey);
                    queryDiscarts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {

                            if (dataSnapshot3.exists()) {
                                no_load = new LinkedHashMap<>();

                                for (DataSnapshot snapshot3 : dataSnapshot3.getChildren()) {
                                    no_load.put(snapshot3.getKey(), snapshot3.getValue());
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //sacamos los usuarios - los match y los discarts (son los que se presentaran en las cartas de presentación)
                    FirebaseDatabase.getInstance().getReference("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot4) {


                            if (dataSnapshot4.exists()) {

                                for (DataSnapshot snapshot4 : dataSnapshot4.getChildren()) {

                                    String key = snapshot4.getKey();

                                    if ((no_load == null || !no_load.containsKey(key)) && !key.equals(miPerroKey)) {

                                        misPerros.put(snapshot4.getKey(), snapshot4.getValue(Perro.class));
                                    }
                                }

                                //seguir por aqui
                                Log.i("TUSA", String.valueOf(misPerros.size()));


                                //obtenemos la info del primer perro de los perros pendientes de catalogar
                                Perro perroInicio = (Perro) misPerros.entrySet().iterator().next().getValue();


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

                //iniciamos la actividad para ver mi perfil
                Intent actividadMiPerfil = new Intent(getApplicationContext(),MiPerfil.class);
                actividadMiPerfil.putExtra("miPerroKey", miPerroKey);
                startActivity(actividadMiPerfil);

                
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


                primerPerro = misPerros.entrySet().iterator().next();

                misPerrosVistos.put(primerPerro.getKey(), primerPerro.getValue());

                misPerros.remove(primerPerro.getKey());

                if (misPerros.size() > 0) {
                    //cargamos la foto del primer perro
                    primerPerro = misPerros.entrySet().iterator().next();//volvemos a obtener el primer perro porque ha cambiado
                    cargarFotoPorEmail(((Perro) primerPerro.getValue()).getEmail());
                    txtEmail.setText("Email: " + ((Perro) primerPerro.getValue()).getEmail());
                    txtGenero.setText("Género: " + ((Perro) primerPerro.getValue()).getGenero());
                    txtNombre.setText("Nombre: " + ((Perro) primerPerro.getValue()).getNombre());
                    txtRaza.setText("Raza: " + ((Perro) primerPerro.getValue()).getRaza());
                } else {
                    //si no quedan perros volvemos a cargar la lista de perros con los perros vistos

                    misPerros.putAll(misPerrosVistos);//pasamos todos los perros vistos a la lista de perros
                    misPerrosVistos.clear();//limpiamos la lista de perros vistos

                    //cargamos la foto del primer perro
                    primerPerro = misPerros.entrySet().iterator().next();//volvemos a obtener el primer perro porque ha cambiado
                    cargarFotoPorEmail(((Perro) primerPerro.getValue()).getEmail());
                    txtEmail.setText("Email: " + ((Perro) primerPerro.getValue()).getEmail());
                    txtGenero.setText("Género: " + ((Perro) primerPerro.getValue()).getGenero());
                    txtNombre.setText("Nombre: " + ((Perro) primerPerro.getValue()).getNombre());
                    txtRaza.setText("Raza: " + ((Perro) primerPerro.getValue()).getRaza());
                }


                break;


            case R.id.btMatch:

                //creamos un match nuevo para el perro que estamos visualizando

                misPerros.putAll(misPerrosVistos);//OJO! cargamos los perros vistos tmb


                if (misPerros.size() > 0) {

                    keyDelPerroActual = misPerros.entrySet().iterator().next().getKey();


                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    builder = new AlertDialog.Builder(MainActivity.this);
                    // Get the layout inflater
                    LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                    //obtenemos la vista del layout personalizado pasandole la ruta al inflates
                    View customView = inflater.inflate(R.layout.dialog_mensaje, null);

                    //obtenemos el cuadro de texto a traves de la vista del layout personalizado, nos obliga a que sea final para utilizarlo en el onClick despues
                    final EditText txtMensaje = customView.findViewById(R.id.txtMatch);

                    builder.setView(customView)


                            // Add action buttons
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    String mensaje = txtMensaje.getText().toString();


                                    FirebaseDatabase.getInstance().getReference("matches").child(keyDelPerroActual).child(miPerroKey).child("mensaje").setValue(mensaje);
                                    FirebaseDatabase.getInstance().getReference("no_load").child(miPerroKey).child(keyDelPerroActual).setValue("match");

                                    misPerros.remove(keyDelPerroActual);

                                    if (misPerros.size() > 0) {
                                        //cargamos la foto del primer perro
                                        primerPerro = misPerros.entrySet().iterator().next();//volvemos a obtener el primer perro porque ha cambiado
                                        cargarFotoPorEmail(((Perro) primerPerro.getValue()).getEmail());
                                        txtEmail.setText("Email: " + ((Perro) primerPerro.getValue()).getEmail());
                                        txtGenero.setText("Género: " + ((Perro) primerPerro.getValue()).getGenero());
                                        txtNombre.setText("Nombre: " + ((Perro) primerPerro.getValue()).getNombre());
                                        txtRaza.setText("Raza: " + ((Perro) primerPerro.getValue()).getRaza());
                                    } else {
                                        //cargamos la foto del comodin ya que no hay perros para cargar
                                        imagenPerro.setImageResource(R.drawable.comodindog);
                                        cargarFotoPorEmail("");
                                        txtEmail.setText("Email: ");
                                        txtGenero.setText("Género: ");
                                        txtNombre.setText("Nombre: ");
                                        txtRaza.setText("Raza: ");
                                    }

                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    //builder.create();
                    builder.show();


                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//                    FirebaseDatabase.getInstance().getReference("matches").child(keyDelPerroActual).child(miPerroKey).child("mensaje").setValue("hola");
//                    FirebaseDatabase.getInstance().getReference("no_load").child(miPerroKey).child(keyDelPerroActual).setValue("match");
//
//                    misPerros.remove(keyDelPerroActual);
//
//                    if (misPerros.size() > 0) {
//                        //cargamos la foto del primer perro
//                        primerPerro = misPerros.entrySet().iterator().next();//volvemos a obtener el primer perro porque ha cambiado
//                        cargarFotoPorEmail(((Perro) primerPerro.getValue()).getEmail());
//                        txtEmail.setText("Email: " + ((Perro) primerPerro.getValue()).getEmail());
//                        txtGenero.setText("Género: " + ((Perro) primerPerro.getValue()).getGenero());
//                        txtNombre.setText("Nombre: " + ((Perro) primerPerro.getValue()).getNombre());
//                        txtRaza.setText("Raza: " + ((Perro) primerPerro.getValue()).getRaza());
//                    } else {
//                        //cargamos la foto del comodin ya que no hay perros para cargar
//                        imagenPerro.setImageResource(R.drawable.comodindog);
//                        cargarFotoPorEmail("");
//                        txtEmail.setText("Email: ");
//                        txtGenero.setText("Género: ");
//                        txtNombre.setText("Nombre: ");
//                        txtRaza.setText("Raza: ");
//                    }

                }


                break;


            case R.id.btDiscart:


                //creamos un match nuevo para el perro que estamos visualizando

                misPerros.putAll(misPerrosVistos);//OJO! cargamos los perros vistos tmb


                if (misPerros.size() > 0) {

                    String keyDelPerroActual = misPerros.entrySet().iterator().next().getKey();

                    //FirebaseDatabase.getInstance().getReference("discarts").child(keyDelPerroActual).child(miPerroKey).setValue(miPerro.getEmail());
                    FirebaseDatabase.getInstance().getReference("no_load").child(miPerroKey).child(keyDelPerroActual).setValue("discart");


                    misPerros.remove(keyDelPerroActual);

                    if (misPerros.size() > 0) {
                        //cargamos la foto del primer perro
                        primerPerro = misPerros.entrySet().iterator().next();//volvemos a obtener el primer perro porque ha cambiado
                        cargarFotoPorEmail(((Perro) primerPerro.getValue()).getEmail());
                        txtEmail.setText("Email: " + ((Perro) primerPerro.getValue()).getEmail());
                        txtGenero.setText("Género: " + ((Perro) primerPerro.getValue()).getGenero());
                        txtNombre.setText("Nombre: " + ((Perro) primerPerro.getValue()).getNombre());
                        txtRaza.setText("Raza: " + ((Perro) primerPerro.getValue()).getRaza());
                    } else {
                        //cargamos la foto del comodin ya que no hay perros para cargar
                        imagenPerro.setImageResource(R.drawable.comodindog);
                        cargarFotoPorEmail("");
                        txtEmail.setText("Email: ");
                        txtGenero.setText("Género: ");
                        txtNombre.setText("Nombre: ");
                        txtRaza.setText("Raza: ");
                    }

                }


                break;


        }


    }


//        //METODO QUE SACA UN DIALOGO PARA MANDAR UN MENSAJE
//        private static EditText text = null;
//        private String resultado = "";
//
//        private String sacarDialogo() {
//
//            // inflamos el custom dialog
//            final Dialog dialog = new Dialog(MainActivity.this);
//            dialog.setContentView(R.layout.dialog_mensaje);
//            dialog.setTitle("Mensaje");
//
//            // capturamos los objetos
//            text = (EditText) dialog.findViewById(R.id.txtMatch);
//
//            //boton aceptar
//            Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
//            // if button is clicked, close the custom dialog
//            btnOK.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    resultado = text.getText().toString();
//                }
//            });
//
//            //boton cancelar
//            Button btnCancel = (Button) dialog.findViewById(R.id.btnOK);
//            // if button is clicked, close the custom dialog
//            btnCancel.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    resultado = "";
//                    dialog.cancel();
//                }
//            });
//
//            dialog.show();
//
//            return resultado;
//        }


}




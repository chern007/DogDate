package com.example.carlos_hc.dogdate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import Objetos.Perro;

public class EditarPerfil extends AppCompatActivity {

    TextView miEmail;
    ImageView foto;
    EditText nombre;
    EditText raza;
    EditText genero;

    String rutaImagen;

    Query queryMiPerro;
    String keyMiPerro;

    Bitmap fotoBitmap;

    static final int REQUEST_IMAGE_GET = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  Configuración");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        miEmail = findViewById(R.id.txtEmail);
        foto = findViewById(R.id.imgPerroEdit);
        nombre = findViewById(R.id.txtNombre);
        raza = findViewById(R.id.txtRaza);
        genero = findViewById(R.id.txtGenero);


        //***
        //lo convertimos a Bitmap
//        fotoBitmap = BitmapFactory.decodeFile("/storage/emulated/0/Download/puticlub.jpg");
//        foto.setImageBitmap(fotoBitmap);

        //ponemos el email del perro actual
        miEmail.setText(getIntent().getStringExtra("miPerroEmail"));

        //obtenemos los datos de nuestro perro por medio de un objeto Perro
        queryMiPerro = FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("email").equalTo(miEmail.getText().toString());
        queryMiPerro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //sacamos los datos del perro si este existe solamente una coincidencia
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        keyMiPerro = snapshot.getKey();
                        Perro tmp = snapshot.getValue(Perro.class);

                        //si tiene el nombre, la raza y el genero metemos los datos
                        if (!tmp.getNombre().equals("") && !tmp.getRaza().equals("") && !tmp.getGenero().equals("")) {

                            cargarFotoPorEmail(tmp.getEmail());
                            nombre.setText(tmp.getNombre());
                            raza.setText(tmp.getRaza());
                            genero.setText(tmp.getGenero());

                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    public void obtenerFotoPath(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), REQUEST_IMAGE_GET);

    }

    //declaramos el uri fuera para poderlo utilizar en los siguientes dos metodos
    Uri data;

    //recogemos la info del Intent de coger la imagen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GET) {

                data = result.getData();

                String tusa = getUriRealPath(data);


                if (tusa.endsWith("jpg")) {

                    File myFile = new File(tusa);

                    rutaImagen = myFile.getAbsolutePath();//guardamos la ruta de la imagen para saber que se ha cargado una imagen

//                    //lo convertimos a Bitmap
//                    fotoBitmap = BitmapFactory.decodeFile(rutaImagen);

                    foto.setImageURI(data);

                } else {
                    Toast.makeText(this, "No has escogido un archivo .jpg", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void subirFotoaNube(String ruta) {

        if (ruta != null) {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference().child("dogDate");


            StorageReference riversRef = storageRef.child(miEmail.getText().toString() + ".jpg");


            UploadTask uploadTask = riversRef.putFile(data);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Toast.makeText(EditarPerfil.this, "Error al subir la foto.", Toast.LENGTH_LONG).show();

                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(EditarPerfil.this, "La foto se ha subido correctamente", Toast.LENGTH_LONG).show();


                    //preparamos el intent que tiene que recibir la actividad anterior
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", miEmail.getText().toString());
                    setResult(Activity.RESULT_OK, returnIntent);

                    //cerramos la actividad
                    EditarPerfil.this.finish();
                }
            });

        } else {

            //preparamos el intent que tiene que recibir la actividad anterior
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", miEmail.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);

            finish();
        }

    }


    public void guardarCambios(View view) {

        //subimos la foto a la nube
        if (foto.getDrawable() != null && !nombre.getText().toString().equals("") && !raza.getText().toString().equals("") && !genero.getText().toString().equals("")) {


            //obtenemos la referencia de nuestro perro de firebase
            DatabaseReference referenciaMiPerro = FirebaseDatabase.getInstance().getReference("usuarios").child(keyMiPerro);

            //escribimos el nombre
            referenciaMiPerro.child("nombre").setValue(nombre.getText().toString());
            //escribimos el genero
            referenciaMiPerro.child("genero").setValue(genero.getText().toString());
            //escribimos la raza
            referenciaMiPerro.child("raza").setValue(raza.getText().toString());

            subirFotoaNube(rutaImagen);


        } else {

            Toast.makeText(EditarPerfil.this, "Por favor selecciona una foto.", Toast.LENGTH_LONG).show();

        }


    }


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
                .into(foto);


    }


    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    /* Get uri related content real local file path. */
    private String getUriRealPath(Uri uri) {

        if (uri == null){

            return null;

        }else{

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,projection, null, null, null);

            if (cursor != null) {

                int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                return cursor.getString(col_index);

            }else{

                return null;
            }


        }

    }


}

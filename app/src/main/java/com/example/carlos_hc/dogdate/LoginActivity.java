package com.example.carlos_hc.dogdate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Objetos.Perro;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView txtEmail;
    Button btnSigIn;
    private EditText txtPassword;
    FirebaseAuth mAuth;
    TextView nuevoUsuario;
    String ultimoId;

    EditText txtEmailRegistro;
    EditText txtContraseñaRegistro;
    EditText txtContraseñaRegistro2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nuevoUsuario = findViewById(R.id.txtNuevoUsuario);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.dogdatelogo_round);
        getSupportActionBar().setTitle("  DogDate");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mAuth = FirebaseAuth.getInstance();


        btnSigIn = (Button) findViewById(R.id.btSign_in);
        txtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);

        txtEmail.setText("ringo@gmail.com");//para probar
        txtPassword.setText("261187");//para probar

        btnSigIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //sacamos el usuario y la pass de la actividad
                final String user = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();


                //contrastamos contra la base de datos
                mAuth.signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, "Login correcto", Toast.LENGTH_LONG).show();
                                    //iniciamos la actividad principal de la aplicacion
                                    Intent actividadPrincipal = new Intent(getApplicationContext(), MainActivity.class);
                                    actividadPrincipal.putExtra("email", user);
                                    startActivity(actividadPrincipal);
                                }

                                // [START_EXCLUDE]
                                if (!task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, "Login erroneo", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });


    }

    public void hacerNuevoUsuario(View view) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        //obtenemos la vista del layout personalizado pasandole la ruta al inflates
        View customView = inflater.inflate(R.layout.formulario_registro, null);

        //obtenemos el cuadro de texto a traves de la vista del layout personalizado, nos obliga a que sea final para utilizarlo en el onClick despues
        txtEmailRegistro = customView.findViewById(R.id.txtEmailRegistro);
        txtContraseñaRegistro = customView.findViewById(R.id.txtContraseñaRegistro);
        txtContraseñaRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtContraseñaRegistro2 = customView.findViewById(R.id.txtContraseñaRegistro2);
        txtContraseñaRegistro2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(customView)


                // Add action buttons
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //vemos si esta bien formado la direccion de email
                        if (formatoEmail(txtEmailRegistro.getText().toString())) {

                            //comprobmos  que las dos contraseñas introducidas sean las mismas
                            if ( txtContraseñaRegistro.getText().toString().equals(txtContraseñaRegistro2.getText().toString())) {

                                Query myTopPostsQuery = FirebaseDatabase.getInstance().getReference("usuarios").orderByKey().limitToLast(1);
                                myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {

                                            for (DataSnapshot elemento : dataSnapshot.getChildren()) {

                                                ultimoId = elemento.getKey();

                                            }

                                            //creamos un nuevo usuario en el modulo de autentificacion de firebase
                                            mAuth = FirebaseAuth.getInstance();
                                            mAuth.createUserWithEmailAndPassword(txtEmailRegistro.getText().toString(), txtContraseñaRegistro.getText().toString())
                                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // Sign in success, update UI with the signed-in user's information
                                                                Log.d("INFO:", "createUserWithEmail:success");


                                                                //AÑADIMOS UN NUEVO NODO PERRO A LA BASE DE DATOS
                                                                //sumamos 1 al ultimo Id
                                                                int nuevoId = Integer.valueOf(ultimoId) + 1;

                                                                //creamos un nuevo perro

                                                                Perro nuevoPerro = new Perro(nuevoId, txtEmailRegistro.getText().toString(), "", "", "");

                                                                //registramo el nuevo perro den la base de datos
                                                                FirebaseDatabase.getInstance().getReference("usuarios").child(String.valueOf(nuevoId)).setValue(nuevoPerro);

                                                                Toast.makeText(LoginActivity.this, "Nuevo usuario registrado correctamente.", Toast.LENGTH_LONG).show();

                                                            } else {
                                                                // If sign in fails, display a message to the user.
                                                                Log.d("INFO:", "createUserWithEmail:success");

                                                                Toast.makeText(LoginActivity.this, "Error al registrar nuevo usuario.", Toast.LENGTH_LONG).show();

                                                            }


                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else{

                                Toast.makeText(LoginActivity.this, "Las contraseñas introducidas no son iguales, por favor vuelva a introducirlas", Toast.LENGTH_LONG).show();
                                //vaciamos el contenido
                                txtContraseñaRegistro.setText("");
                                txtContraseñaRegistro2.setText("");

                            }

                        } else {

                            Toast.makeText(LoginActivity.this, "El formato del email no es correcto.", Toast.LENGTH_LONG).show();

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


    }

    private boolean formatoEmail(String email) {

        //patron de busqueda para emails
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {

            return true;

        } else {

            return false;

        }
    }


}



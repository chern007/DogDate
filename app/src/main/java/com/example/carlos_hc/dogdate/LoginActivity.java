package com.example.carlos_hc.dogdate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private AutoCompleteTextView txtEmail;
    Button btnSigIn;
    private EditText txtPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        btnSigIn = (Button) findViewById(R.id.btSign_in);
        txtEmail = (AutoCompleteTextView) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);


        btnSigIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //sacamos el usuario y la pass de la actividad
                String user = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();


                //contrastamos contra la base de datos
                mAuth.signInWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this,"OK",Toast.LENGTH_LONG).show();

                                    //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                }

                                // [START_EXCLUDE]
                                if (!task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this,"NOOOO",Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        });


    }




    private void sacaMensaje(){
        Toast.makeText(this,"NOOOO",Toast.LENGTH_LONG).show();
    }


}



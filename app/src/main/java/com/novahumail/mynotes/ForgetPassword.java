package com.novahumail.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ForgetPassword extends AppCompatActivity {

    private EditText mforgetpasswordET;
    private Button mPasswordRecoverBTN;
    private TextView mGoBackToLoginTV;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.background));

        mforgetpasswordET = findViewById(R.id.email);
        mPasswordRecoverBTN = findViewById(R.id.recover);
        mGoBackToLoginTV = findViewById(R.id.goToLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        mGoBackToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(h);
            }
        });

        mPasswordRecoverBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mforgetpasswordET.getText().toString().trim();
                if (mail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your Email first", Toast.LENGTH_SHORT).show();
                } else {
//                    we have to send recover password email
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email sent, You can recover your password using email", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }else{
                                Toast.makeText(ForgetPassword.this, "Email is wrong or account not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }
}
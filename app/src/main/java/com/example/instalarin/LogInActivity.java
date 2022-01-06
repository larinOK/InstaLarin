package com.example.instalarin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private TextView registerText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.logInEmail);
        password = findViewById(R.id.logInPassword);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.register_user);

        mAuth = FirebaseAuth.getInstance();
    }


    public void setRegisterText(View v) {
        startActivity(new Intent(LogInActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void setButtonLogin(View v) {
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();

        if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
            Toast.makeText(this, "Empty field", Toast.LENGTH_LONG).show();
        }
        else{
            loginUser(txt_email, txt_password);
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("man", email);
                    Toast.makeText(LogInActivity.this, "Welcome back to InstaLarin", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }



}
package com.example.instalarin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerUsername;
    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;
    private Button buttonRegister;
    private TextView loginUser;

    ProgressDialog pd;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsername = findViewById(R.id.registerUsername);
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        loginUser = findViewById(R.id.login_user);

        //FirebaseApp.initializeApp(this);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);


    }

    public void setLoginUser(View v) {
        Intent i = new Intent(this, LogInActivity.class);
        startActivity(i);
    }

    public void setButtonRegister(View v) {
        String username = registerUsername.getText().toString();
        String name = registerName.getText().toString();
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        if(TextUtils.isEmpty(username) || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Empty field", Toast.LENGTH_LONG).show();
        }
        else if (password.length() < 6) {
            Toast.makeText(this, "Password is too short", Toast.LENGTH_LONG).show();
        }
        else {
            registerUser(username, name, email, password);
        }
    }

    private void registerUser(String username, String name, String email, String password) {

        pd.setMessage("Please wait");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authResult -> {

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("email", email);
            map.put("username", username);
            map.put("id", mAuth.getCurrentUser().getUid());
            map.put("bio", "");
            map.put("imageUrl", "default");

            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    pd.dismiss();
                    Log.d("man", name);
                    Toast.makeText(RegisterActivity.this, "Complete your profile for a better experience", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                    startActivity(intent);

                    finish();
                }
            });
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
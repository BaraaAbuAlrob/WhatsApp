package com.baraa.firebase.whatsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout Email, Password;
    TextView SignupActivity;

    Button SignIn;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.EmailLogin);
        Password = findViewById(R.id.PasswordLogin);

        SignupActivity = findViewById(R.id.signUpActivity);

        SignIn = findViewById(R.id.signInBtn);

        auth = FirebaseAuth.getInstance();

        SignupActivity.setOnClickListener(v ->{

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        SignIn.setOnClickListener(v -> {

            String email = Objects.requireNonNull(Email.getEditText()).getText().toString();
            String pass = Objects.requireNonNull(Password.getEditText()).getText().toString();

            checkUser(email, pass);
        });
    }

    private void checkUser(String email, String pass) {

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {

           if(task.isSuccessful()){
               Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
               finish();
           } else {
               Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
           }
        });
    }
}
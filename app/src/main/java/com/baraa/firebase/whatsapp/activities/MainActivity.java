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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextInputLayout Username, Email, Status, Password;
    String UsernameValue, EmailValue, StatusValue, PasswordValue;
    Button SignUp;

    TextView SignInActivity;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = findViewById(R.id.TextInputNameLayout);
        Email = findViewById(R.id.TextInputEmailLayout);
        Status = findViewById(R.id.TextInputStatusLayout);
        Password = findViewById(R.id.TextInputPassword);
        SignInActivity = findViewById(R.id.signInActivity);

        SignUp = findViewById(R.id.signUpBtn);

        auth = FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        SignUp.setOnClickListener(v -> {

            UsernameValue = Username.getEditText().getText().toString();
            EmailValue = Email.getEditText().getText().toString();
            StatusValue = Status.getEditText().getText().toString();
            PasswordValue = Password.getEditText().getText().toString();

            if(!UsernameValue.isEmpty() && !EmailValue.isEmpty() && !StatusValue.isEmpty() && !PasswordValue.isEmpty())
                register(UsernameValue, EmailValue, StatusValue, PasswordValue);
        });

        SignInActivity.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void register(String usernameValue, String emailValue, String statusValue, String passwordValue) {

        auth.createUserWithEmailAndPassword(EmailValue, PasswordValue).addOnCompleteListener(task1 -> {

            if (task1.isSuccessful()){

                FirebaseUser firebaseUser = auth.getCurrentUser();
                String userId = firebaseUser.getUid();

                reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("id", userId);
                hashMap.put("username", usernameValue);
                hashMap.put("email", emailValue);
                hashMap.put("status", statusValue);
                hashMap.put("password", passwordValue);
                hashMap.put("imgUrl", "default");
                hashMap.put("state", "offline");
                hashMap.put("isInChat", "no");

                reference.setValue(hashMap).addOnCompleteListener(task2 -> {

                    if(task2.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    } else
                        Toast.makeText(MainActivity.this, "SignUp failed!", Toast.LENGTH_LONG).show();
                });

            } else
                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
        });
    }
}
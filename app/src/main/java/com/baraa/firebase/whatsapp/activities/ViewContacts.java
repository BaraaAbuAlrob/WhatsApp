package com.baraa.firebase.whatsapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.adapters.userAdapter;
import com.baraa.firebase.whatsapp.modelClass.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewContacts extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    userAdapter adapter;
    List<User> mUsers;

    private final String USER_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        state("online");

        toolbar = (Toolbar) findViewById(R.id.viewContactToolbar);
        CircleImageView back = findViewById(R.id.contacts_arrowBack);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewContacts.this));

        mUsers = new ArrayList<>();
        getUsers();

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            toolbar.setTitleTextColor(Color.WHITE);
        }

        back.setOnClickListener(v -> {
            Intent intent = new Intent(ViewContacts.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void getUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getId().equals(firebaseUser.getUid()))
                        mUsers.add(user);
                }
                adapter = new userAdapter(ViewContacts.this, mUsers,false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewContacts.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void state(String state){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("users").child(USER_ID);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("state",state);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        state("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        state("offline");
    }
}
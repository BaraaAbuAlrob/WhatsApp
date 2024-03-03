package com.baraa.firebase.whatsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.adapters.MessageAdapter;
import com.baraa.firebase.whatsapp.modelClass.Chat;
import com.baraa.firebase.whatsapp.modelClass.User;
import com.bumptech.glide.Glide;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private TextView username;

    private ImageView btn_send;
    private EditText text_send;

    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private Intent intent;
    private String userid, isInChatS;
    private DatabaseReference reference;

    private ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        state("online");
        inChat();

        profileImg = findViewById(R.id.profile_img);
        username = findViewById(R.id.Username);

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);  // <<<<<<<<<< Important >>>>>>>>>>
        recyclerView.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(v -> {
            String msg = text_send.getText().toString();
            if(!msg.isEmpty())
                sendMessage(firebaseUser.getUid(), userid, msg);

            text_send.setText("");
        });

        Toolbar toolbar = findViewById(R.id.MessageToolbar);
        CircleImageView back = findViewById(R.id.message_arrowBack);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        back.setOnClickListener(v -> {
            intent = new Intent(MessageActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        });

        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                isInChatS = user.getIsInChat();

                if (user.getImgUrl().equals("default"))
                    profileImg.setImageResource(R.drawable.user);
                else if (!isDestroyed())
                    Glide.with(MessageActivity.this).load(user.getImgUrl()).into(profileImg);

                LoadMessages(firebaseUser.getUid(), userid, user.getImgUrl());
                seenMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // End of onCreate method(...)

    private void seenMessage(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                            && isReceiverInChat()){

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);

                        reference.child(Objects.requireNonNull(dataSnapshot.getKey())).updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("state",false);

        reference.child("Chats").push().setValue(hashMap);
    }

    private void LoadMessages(String myId, String userId, String imgUrl){

        mChat = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        mChat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imgUrl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void state(String state){
        reference = FirebaseDatabase.getInstance().getReference("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("state",state);

        reference.updateChildren(hashMap);
    }

    private void inChat() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("isInChat","yes");
        reference.updateChildren(hashMap);
    }

    private void notInChat() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("isInChat","no");
        reference.updateChildren(hashMap);
    }

    private boolean isReceiverInChat() {
        return isInChatS.equals("yes");
    }

    @Override
    protected void onResume() {
        super.onResume();
        state("online");
        inChat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        state("offline");
        notInChat();
        reference.removeEventListener(seenListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        state("offline");
        notInChat();
        reference.removeEventListener(seenListener);
    }
}
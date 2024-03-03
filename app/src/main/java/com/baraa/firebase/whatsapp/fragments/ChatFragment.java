package com.baraa.firebase.whatsapp.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.adapters.userAdapter;
import com.baraa.firebase.whatsapp.modelClass.Chat;
import com.baraa.firebase.whatsapp.modelClass.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    userAdapter userAdapter;
    List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    List<String> userIdsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Here write your code

        recyclerView = view.findViewById(R.id.chats_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userIdsList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userIdsList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(firebaseUser.getUid())){
                        if(!userIdsList.contains(chat.getReceiver()))
                            userIdsList.add(chat.getReceiver());
                    }
                    else if(chat.getReceiver().equals(firebaseUser.getUid())){
                        if(!userIdsList.contains(chat.getSender()))
                            userIdsList.add(chat.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readChats() {

        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUsers.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    for (int i=0; i<userIdsList.size(); i++){
                        if(user.getId().equals(userIdsList.get(i))){
                            if(mUsers.size()!= 0) {
                                if (!mUsers.contains(user)) {
                                    mUsers.add(user);
                                }
                            }
                            else
                                mUsers.add(user);
                        }
                    }
                }
                userAdapter = new userAdapter(requireContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.baraa.firebase.whatsapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.activities.MessageActivity;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder> {
    private final Context mContext;
    private final List<User> mUsers;

    // use the adapter from ChatFragment or from ViewContacts activity?
    private final boolean fromChatFragment;
    private String lastMsg;

    public userAdapter(Context mContext, List<User> userList, boolean fromChatFragment){
        this.mContext = mContext;
        this.mUsers = userList;
        this.fromChatFragment = fromChatFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.contactName.setText(user.getUsername());

        if(user.getImgUrl().equals("default")){
            holder.profileImg.setImageResource(R.drawable.user);

        } else {
            Glide.with(mContext).load(user.getImgUrl()).into(holder.profileImg);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("userid", user.getId());
            mContext.startActivity(intent);
        });

        if(fromChatFragment){
            if(user.getState().equals("online")){
                holder.img_off.setVisibility(View.GONE);
                holder.img_on.setVisibility(View.VISIBLE);
            } else if (user.getState().equals("offline")) {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
            lastMessage(user.getId(), holder.contactStatus);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            holder.contactStatus.setText(user.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void lastMessage(String userid, TextView lastMessage){
        lastMsg = "";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid) ||
                            chat.getSender().equals(userid) && chat.getReceiver().equals(firebaseUser.getUid()))
                        lastMsg = chat.getMessage();
                }
                if (lastMsg.equals("")) {
                    lastMessage.setText("No messages");
                } else {
                    lastMessage.setText(lastMsg);
                }
                lastMsg = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView contactName, contactStatus;
        ImageView profileImg;
        CircleImageView img_on,img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactName = itemView.findViewById(R.id.ContactName);
            contactStatus = itemView.findViewById(R.id.ContactStatus);
            profileImg = itemView.findViewById(R.id.imgProfile);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
}
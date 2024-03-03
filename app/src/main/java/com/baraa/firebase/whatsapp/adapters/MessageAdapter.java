package com.baraa.firebase.whatsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baraa.firebase.project.com.baraa.firebase.whatsapp.R;
import com.baraa.firebase.whatsapp.modelClass.Chat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context mContext;
    private final List<Chat> chatList;
    private final String imgUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> chatList, String imgUrl){
        this.mContext = mContext;
        this.chatList = chatList;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);

        else
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.show_message.setText(chatList.get(position).getMessage());

        if(imgUrl.equals("default"))
            holder.profileImg.setImageResource(R.drawable.user);

        else
            Glide.with(mContext).load(imgUrl).into(holder.profileImg);

        if(position == chatList.size()-1){
            if(chatList.get(position).getSeen())
                holder.txt_seen.setText("Seen");
            else
                holder.txt_seen.setText("Delivered");

        } else
            holder.txt_seen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {

        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView show_message, txt_seen;
        ImageView profileImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.chat_showMessage);
            profileImg = itemView.findViewById(R.id.chat_profileImg);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(chatList.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
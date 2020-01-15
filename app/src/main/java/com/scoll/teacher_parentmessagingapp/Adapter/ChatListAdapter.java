// where we manage and display the chat list (same as chat collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scoll.teacher_parentmessagingapp.ChatActivity;
import com.scoll.teacher_parentmessagingapp.Model.ChatObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

//    // passing the variables
//    public static final int MSG_TYPE_LEFT = 0;
//    public static final int MSG_TYPE_RIGHT = 1;

    private ArrayList<ChatObject> chatList;

    // constructor
    public ChatListAdapter(ArrayList<ChatObject> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // making the call for the item_chat
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        // viewHolder variable
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        // grabs the position we need on the array chatList e displays it
        holder.mTitle.setText(chatList.get(position).getChatId());

        // on click listener for when user clicks on a chat, goes to messaging on ChatActivity
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);

                // get a bundle of variables that we want to send with the intent above
                Bundle bundle = new Bundle();
                bundle.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatId());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (chatList == null) ? 0 : chatList.size();
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public LinearLayout mItemLayout;

        public ChatListViewHolder(View view){
            super(view);
            mTitle = view.findViewById(R.id.title);
            mItemLayout = view.findViewById(R.id.itemLayout);
        }
    }
}
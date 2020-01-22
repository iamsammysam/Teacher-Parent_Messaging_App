package com.scoll.teacher_parentmessagingapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scoll.teacher_parentmessagingapp.Adapter.ChatListAdapter;
import com.scoll.teacher_parentmessagingapp.Adapter.UserListAdapter;
import com.scoll.teacher_parentmessagingapp.Model.ChatObject;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.Model.UserObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    // recyclerView variables
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mChatListAdapter;
    private ArrayList<ChatObject> chatList;

    DatabaseReference referenceDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chatList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initializing messageList
        chatList = new ArrayList<>();

        mChatListAdapter = new ChatListAdapter(getContext(), chatList);
        recyclerView.setAdapter(mChatListAdapter);

        // calling the function contactList
        getUserChatList();
        return view;
    }

    private void getUserChatList(){
        // listener
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        referenceDB.addValueEventListener(new ValueEventListener() {
            @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String title = "Chat between Teacher and Parent";

                            // loops through the chat ids
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                        // creating a chatObject
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        chatList.add(mChat);

                        // updates mChatListAdapter and notifies that something changed
                        mChatListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}


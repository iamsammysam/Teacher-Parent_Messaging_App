// main page activity

package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scoll.teacher_parentmessagingapp.Adapter.ChatListAdapter;
import com.scoll.teacher_parentmessagingapp.Model.ChatObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // variables
    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    TextView username;
    ArrayList<ChatObject> chatList;
    FirebaseAuth firebaseUser;
    DatabaseReference referenceDB;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //initializing variables
        chatList = new ArrayList<>();
        username = findViewById(R.id.username);
        firebaseUser = FirebaseAuth.getInstance();
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");

        // findUser contacts onClick listener
        Button mFindUser = findViewById(R.id.findUser);
        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), Main3Activity.class));
            }
        });

        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }

    private void getUserChatList(){
        // listener
        // Log.e("MainActivity", "contacts");
        referenceDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    // loops through the chat ids
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                        // creating a chatObject
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        chatList.add(mChat);

                        // updates mChatListAdapter and notifies that something changed
                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // getting permission to read contact list from phone
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions() {
        requestPermissions(new String[] {
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
    }

    //function to initialize menu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                firebaseUser.signOut();

                // making user go to a different page after logout
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                //Toast.makeText(this, "Logging out... See you next time!", Toast.LENGTH_LONG).show();
                finish();
                return true;
        }
        return false;
    }

    // function to initialize RecyclerView
    private void initializeRecyclerView() {
        mChatList = findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);

        mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }
}
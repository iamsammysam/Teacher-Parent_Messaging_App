// contact list/find user activity

package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scoll.teacher_parentmessagingapp.Adapter.UserListAdapter;
import com.scoll.teacher_parentmessagingapp.Model.UserObject;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    // variables
    private RecyclerView userListRecyclerView;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager userListLayoutManager;

    ArrayList<UserObject> userList, contactList;
    DatabaseReference referenceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        referenceDB = FirebaseDatabase.getInstance().getReference().child("user");;

        // initializing contactList (fetches the contacts)
        contactList = new ArrayList<>();

        // initializing userList (fetches the contacts that are users (on the DB))
        userList = new ArrayList<>();

        // calling the functions
        initializeRecyclerView();
        getContactList();
    }

    private void getContactList() {
        Cursor phoneNumbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phoneNumbers.moveToNext()) {
            String name = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");

            UserObject mContact = new UserObject("",name, phoneNumber);
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }

    // function to check fireBase DATABASE to confirm if the user is there
    private void getUserDetails(final UserObject mContact) {

        // fetching data from DB
        Query query = referenceDB.orderByChild("phoneNumber").equalTo(mContact.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phoneNumber = "";
                    String username = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        // looks through all users and returns one user
                        if(childSnapshot.child("phoneNumber").getValue()!= null)
                            phoneNumber = childSnapshot.child("phoneNumber").getValue().toString();

                        if(childSnapshot.child("username").getValue()!= null)
                            username = childSnapshot.child("username").getValue().toString();

                        UserObject mUser = new UserObject(childSnapshot.getKey(), username, phoneNumber);
                        // setting the username to name on phone contact list
                        if (username.equals(phoneNumber))
                            for (UserObject mContactIterator : contactList) {
                                if (mContactIterator.getPhoneNumber().equals(mUser.getPhoneNumber())){
                                    mUser.setUsername(mContactIterator.getUsername());
                                }
                            }

                        userList.add(mUser);

                        // updates mUserListAdapter and notifies that something changed
                        userListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // function to initialize RecyclerView
    private void initializeRecyclerView() {
        userListRecyclerView = findViewById(R.id.userList);
        userListRecyclerView.setNestedScrollingEnabled(false);
        userListRecyclerView.setHasFixedSize(false);

        userListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        userListRecyclerView.setLayoutManager(userListLayoutManager);
        userListAdapter = new UserListAdapter(userList);
        userListRecyclerView.setAdapter(userListAdapter);
    }
}
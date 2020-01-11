// contact list/find user activity

package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Currency;

public class Main3Activity extends AppCompatActivity {

    // variables
    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

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

        // Debugging
        // Log.e("Main3Activity", "contacts");

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
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");

        // fetching data from DB
        Query query = mUserDB.orderByChild("phoneNumber").equalTo(mContact.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phoneNumber = "";
                    String name = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                        // looks through all users and returns one user
                        if(childSnapshot.child("phoneNumber").getValue()!= null)
                            phoneNumber = childSnapshot.child("phoneNumber").getValue().toString();

                        if(childSnapshot.child("name").getValue()!= null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phoneNumber);
                        // setting the username to name on phone contact list
                        if (name.equals(phoneNumber))
                            for (UserObject mContactIterator : contactList) {
                                if (mContactIterator.getPhoneNumber().equals(mUser.getPhoneNumber())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
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
        mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);

        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);

        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}
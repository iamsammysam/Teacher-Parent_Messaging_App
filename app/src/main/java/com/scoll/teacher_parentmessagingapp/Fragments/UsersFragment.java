package com.scoll.teacher_parentmessagingapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
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
import com.scoll.teacher_parentmessagingapp.R;

public class UsersFragment extends Fragment {

    public static UsersFragment newInstance() {
        return new UsersFragment();
    }

    // variables
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mUserListAdapter;
    private ArrayList<UserObject> userList, contactList;

    DatabaseReference referenceDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.userList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initializing contactList (fetches the contacts)
        contactList = new ArrayList<>();

        // initializing userList (fetches the contacts that are users (on the DB))
        userList = new ArrayList<>();

        mUserListAdapter = new UserListAdapter(getContext(), userList);
        recyclerView.setAdapter(mUserListAdapter);

        // calling the function contactList
        getContactList();
        return view;
    }

    private void getContactList() {
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user");
        Cursor phoneNumbers = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phoneNumbers.moveToNext()) {
            String username = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");

            UserObject mContact = new UserObject("", username, phoneNumber, "");
            contactList.add(mContact);
            getUserDetails(mContact);
        }
    }

    // function to check fireBase DATABASE to confirm if the user is there
    private void getUserDetails(final UserObject mContact) {
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user");

        // fetching data from DB
        Query query = referenceDB.orderByChild("phoneNumber").equalTo(mContact.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phoneNumber = "";
                    String username = "";
                    String language = "";

                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        // looks through all users and returns one user
                        if(childSnapshot.child("phoneNumber").getValue()!= null)
                            phoneNumber = childSnapshot.child("phoneNumber").getValue().toString();

                        if(childSnapshot.child("username").getValue()!= null)
                            username = childSnapshot.child("username").getValue().toString();

                        if(childSnapshot.child("language").getValue()!= null)
                            language = childSnapshot.child("language").getValue().toString();

                        UserObject mUser = new UserObject(childSnapshot.getKey(), username, phoneNumber, language);

                        // setting the username to name on phone contact list
                        if (username.equals(phoneNumber))
                            for (UserObject mContactIterator : contactList) {
                                if (mContactIterator.getPhoneNumber().equals(mUser.getPhoneNumber())){
                                    mUser.setUsername(mContactIterator.getUsername());
                                }
                            }

                        userList.add(mUser);

                        // updates mUserListAdapter and notifies that something changed
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
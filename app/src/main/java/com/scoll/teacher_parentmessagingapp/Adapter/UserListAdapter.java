// where we manage and display the user list (same as user collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.scoll.teacher_parentmessagingapp.Model.UserObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    // passing the variable
    ArrayList<UserObject> userList;

    // constructor
    public UserListAdapter(Context context, ArrayList<UserObject> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // making the call for the item_user
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ViewHolder rcv = new ViewHolder(layoutView);
        // viewHolder variable
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // grabs the position we need on the array userList e displays it
        holder.mUsername.setText(userList.get(position).getUsername());
        holder.mPhoneNumber.setText(userList.get(position).getPhoneNumber());

        // on click listener for chat
        holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // returns a unique ID that doesn't exist inside "chat" on the FireBaseDatabase
                String key =  FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                // creates a chat inside user1 and user2 with the same ID
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
            }
        });
    }

    @Override
    public int getItemCount() { return (userList == null) ? 0 : userList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mUsername, mPhoneNumber;
        public LinearLayout mItemLayout;

        public ViewHolder(View view){
            super(view);
            mPhoneNumber = view.findViewById(R.id.phoneNumber);
            mUsername = view.findViewById(R.id.username);
            mItemLayout = view.findViewById(R.id.itemLayout);
        }
    }
}
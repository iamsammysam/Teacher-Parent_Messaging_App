// where we manage and display messages (same as message collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageListViewHolder> {

    // passing the variables
    public final int MSG_TYPE_RIGHT = 0;
    public final int MSG_TYPE_LEFT = 1;

    // initializing messageList
    ArrayList<MessageObject> messageList;

    // constructor
    public MessageAdapter(ArrayList<MessageObject> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // making the call for the item_chat (depending on the firebaseUser position)
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, null, false);
            return new MessageAdapter.MessageListViewHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, null, false);
            return new MessageAdapter.MessageListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());
//        holder.mReceiver.setText(messageList.get(position).getReceiverId());
//        holder.mMessageTime.setText((CharSequence)messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return (messageList == null) ? 0 : messageList.size();
    }

    public class MessageListViewHolder extends RecyclerView.ViewHolder{
        public TextView mMessage, mSender, mReceiver;
        public LinearLayout mItemLayout;

        public MessageListViewHolder(View itemView){
            super(itemView);
            mMessage = itemView.findViewById(R.id.message);
            mSender = itemView.findViewById(R.id.sender);
            //mReceiver = itemView.findViewById(R.id.receiver);
            //mMessageTime = view.findViewById(R.id.messageTime);
            mItemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }

    // getting text message position on messageList
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
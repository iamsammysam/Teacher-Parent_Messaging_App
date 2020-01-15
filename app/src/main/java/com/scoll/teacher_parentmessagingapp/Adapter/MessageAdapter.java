// where we manage and display messages (same as message collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageListViewHolder> {

    // passing the variable
    ArrayList<MessageObject> messageList;

    // Format the date before showing it
//    mMessageTime = messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)");

    // constructor
    public MessageAdapter(ArrayList<MessageObject> messageList){
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // making the call for the item_chat
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageListViewHolder rcv = new MessageListViewHolder(layoutView);
        // viewHolder variable
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, final int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());
//        holder.mMessageTime.setText((CharSequence)messageList.get(position));
    }

    @Override
    public int getItemCount() {
        return (messageList == null) ? 0 : messageList.size();
    }

    // class doesn't need to be public
    public class MessageListViewHolder extends RecyclerView.ViewHolder{
        public TextView mMessage, mSender, mMessageTime;
        public LinearLayout mItemLayout;

        public MessageListViewHolder(View view){
            super(view);
            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            mMessageTime = view.findViewById(R.id.messageTime);
            mItemLayout = view.findViewById(R.id.itemLayout);
        }
    }
}
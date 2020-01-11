// main page activity

package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Main2Activity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // findUser contacts onClick listener
        Button mFindUser = findViewById(R.id.findUser);
        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), Main3Activity.class));
            }
        });

        // logout onClick listener
        Button mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fireBase documentation - user is logged out
                FirebaseAuth.getInstance().signOut();

                // making user go to a different page after logout
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                // clears user access
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                finish();
            }
        });

        getPermissions();
    }

    // getting permission to read contact list from phone
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions() {
        requestPermissions(new String[] {
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
    }
}

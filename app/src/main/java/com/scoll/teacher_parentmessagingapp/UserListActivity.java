// contact list/find user activity

package com.scoll.teacher_parentmessagingapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.scoll.teacher_parentmessagingapp.Fragments.UsersFragment;

public class UserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UsersFragment.newInstance())
                    .commitNow();
        }
    }
}
// login activity

package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText mPhoneNumber, mVerificationCode;
    private Button mSendCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // this will initialize fireBase on the project
        FirebaseApp.initializeApp(this);

        // checking if user is already logged in before
        userIsLoggedIn();

        mPhoneNumber = findViewById(R.id.phoneNumber);
        mVerificationCode = findViewById(R.id.verificationCode);
        mSendCode = findViewById(R.id.sendCode);

        // creating onclick listener for the sendCode button
        mSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerificationId != null)
                    verifyPhoneNumberWithCode();
                else
                    startPhoneNumberVerification();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            // when code is sent to user
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                // changes the name of the button SendCode
                mSendCode.setText("Verify Code");
            }


            @Override
            // success (account is verified)
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            // failure (if something goes wrong, ex. invalid phone number)
            public void onVerificationFailed(FirebaseException e) {
                // this callback is invoked in an invalid request for verification is made,
                mPhoneNumber.setError("Invalid phone number.");
            }
        };
    }

    // fireBase documentation
    // 1) making the call for the onclick listener
    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber.getText().toString(),  // phone to verify
                60,                              // timeout duration
                TimeUnit.SECONDS,                  // unit of timeout
                this,                      // activity
                mCallbacks);
    }

    // 2) creating code verification function
    private void verifyPhoneNumberWithCode() {
        // creating a credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mVerificationCode.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    // 3) creating log in verification function
    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // gets all user information from the sign in
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null){
                        // points to fireBase database inside "user"
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

                        // listener fetches the data once from the DB
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // checking if user is on the DB
                                if(!dataSnapshot.exists()){
                                    Map<String, Object>userMap = new HashMap<>();
                                    userMap.put("phoneNumber", user.getPhoneNumber());
                                    userMap.put("name", user.getPhoneNumber());

                                    // sends the data off to the DB inside the user
                                    mUserDB.updateChildren(userMap);
                                }
                                userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }

                }
            }
        });
    }

    // 4) creating function that checks if user is logged in or not and moves to next page
    private void userIsLoggedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            // user is logged in
            Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
        } else {
            // user is not logged in
            Toast.makeText(this, "Logging out... See you next time!", Toast.LENGTH_LONG).show();
        }
    }
}






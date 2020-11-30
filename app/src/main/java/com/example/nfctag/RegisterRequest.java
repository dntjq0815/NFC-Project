package com.example.nfctag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class RegisterRequest extends AppCompatActivity {

    EditText mPhoneNum, mCertifiNum;
    Button mRequestBtn, mCertifiBtn;

    FirebaseAuth mAuth;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity_verifi);

        mAuth = FirebaseAuth.getInstance();

        mPhoneNum = findViewById(R.id.phone_num);
        mCertifiNum = findViewById(R.id.certifi_num);
        mRequestBtn = findViewById(R.id.request_button);
        mCertifiBtn = findViewById(R.id.certifi_button);

        mRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        mCertifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyCode();
            }
        });

    }



    private void sendVerificationCode() {

        String phoneNum = mPhoneNum.getText().toString().trim();

        if (phoneNum.isEmpty()) {
            Toast.makeText(RegisterRequest.this, "휴대폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNum.length() < 11) {
            Toast.makeText(RegisterRequest.this, "휴대폰 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void VerifyCode() {
        String code = mCertifiNum.getText().toString().trim();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterRequest.this, RegisterActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "인증 성공", Toast.LENGTH_LONG).show();
                }
                else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "인증번호를 확인해주세요", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };
}

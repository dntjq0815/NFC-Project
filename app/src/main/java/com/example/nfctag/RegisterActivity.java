package com.example.nfctag;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    EditText mEmailText, mPasswordText, mPasswordcheckText, mName;
    Button mNextBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //파이어베이스 접근 설정
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference reference = database.getReference("Users");

        mName = findViewById(R.id.user_name);
        mEmailText = findViewById(R.id.user_email);
        mPasswordText = findViewById(R.id.user_paswd);
        mPasswordcheckText = findViewById(R.id.user_paswd_check);
        mNextBtn = findViewById(R.id.register_button);


        //가입버튼 클릭리스너   -->  firebase에 데이터를 저장한다
        mNextBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwdcheck = mPasswordcheckText.getText().toString().trim();

                if (pwd.equals(pwdcheck)) {
                    Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                    mDialog.setMessage("확인중입니다...");
                    mDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mDialog.dismiss();
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();

                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("name", name);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                mDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "중복되는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}


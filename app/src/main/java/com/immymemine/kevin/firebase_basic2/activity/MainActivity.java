package com.immymemine.kevin.firebase_basic2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.immymemine.kevin.firebase_basic2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    // view
    private EditText signupEm, signupPw, signinEm, signinPw;
    private TextView wrong1, wrong2, wrong3;
    // ref
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users");

        initiateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    public static boolean isValidEmail (String email) {
        Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isValidPasswod (String password) {
        // Pattern p = Pattern.compile("^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$");
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,16}");
        // 8 - 16자리 영문 숫자 특수문자 무조건 한번씩 사용 ; 정규식
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private void initiateView () {
        signupEm = findViewById(R.id.signup_em);
        signupPw = findViewById(R.id.signup_pw);
        signinEm = findViewById(R.id.signin_em);
        signinPw = findViewById(R.id.signin_pw);

        wrong1 = findViewById(R.id.wrong1);
        wrong2 = findViewById(R.id.wrong2);
        wrong3 = findViewById(R.id.wrong3);
    }

    public void signup(View view) {
        String email = signupEm.getText().toString();
        String password = signupPw.getText().toString();

        if(!isValidEmail(email)) {
            Toast.makeText(this, "wrong email format", Toast.LENGTH_LONG).show();
            wrong1.setVisibility(View.VISIBLE);
            return;
        } else {
            wrong1.setVisibility(View.INVISIBLE);
        }

        if(!isValidPasswod(password)) {
            Toast.makeText(this, "wrong password format", Toast.LENGTH_LONG).show();
            wrong2.setVisibility(View.VISIBLE);
            return;
        } else
            wrong2.setVisibility(View.INVISIBLE);

        // firebase auth module >>> 사용자 생성
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    // verification mail 발송  ...  완료확인 listener
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "please check verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    // token 생성
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("Token", "=======" + refreshedToken);
                    // token db 저장
                    userRef.child(user.getUid()).setValue(refreshedToken);

                } else {
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Sign-up Error ======", task.getException().getMessage());
                }
            }
        });
    }

    public void signin(View view) {
        String email = signinEm.getText().toString();
        String password = signinPw.getText().toString();

        if(!isValidEmail(email)) {
            Toast.makeText(this, "wrong email format", Toast.LENGTH_LONG).show();
            wrong3.setVisibility(View.VISIBLE);
            return;
        } else {
            wrong3.setVisibility(View.INVISIBLE);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                Intent intent = new Intent(MainActivity.this, StorageActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "You should verify your e-mail address.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Sign-in Error ======", task.getException().getMessage());
                        }
                    }
                });
    }

    public void getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }
}

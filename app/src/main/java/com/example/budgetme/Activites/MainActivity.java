package com.example.budgetme.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.budgetme.ForgotPassword;
import com.example.budgetme.R;
import com.example.budgetme.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText signIN_LBL_email, signIN_LBL_password;
    private MaterialTextView signIn_LBL_loginQ;
    private MaterialButton signIn_BTN_login, signIn_BTN_SignUp, signIn_BTN_forgotPassword;
    private ImageView main_IMV_appLogo;
    //firebase
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        findViews();
        clickListeners();
    }

    private void clickListeners() {
        //when button pressed move to sign up activity
        signIn_BTN_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
        signIn_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
        signIn_BTN_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin() {
        String email = signIN_LBL_email.getText().toString().trim();
        String password = signIN_LBL_password.getText().toString().trim();

        if (email.isEmpty()) {
            signIN_LBL_email.setError("email is required");
            signIN_LBL_email.requestFocus();
            return;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            signIN_LBL_email.setError("Please provide valid email");
            signIN_LBL_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            signIN_LBL_password.setError("password is required");
            signIN_LBL_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            signIN_LBL_password.setError("Min password length should be 6 chars");
            signIN_LBL_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this,"Check yopur email to verify account",Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "faild to login! please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    public void findViews() {
        signIN_LBL_email = findViewById(R.id.signIN_LBL_email);
        signIN_LBL_password = findViewById(R.id.signIN_LBL_password);
        signIn_BTN_login = findViewById(R.id.signIn_login_BTN);
        signIn_LBL_loginQ = findViewById(R.id.signIn_LBL_loginQ);
        signIn_BTN_SignUp = findViewById(R.id.signIn_BTN_SignUp);
        signIn_BTN_forgotPassword = findViewById(R.id.signIn_BTN_forgotPassword);
        main_IMV_appLogo = findViewById(R.id.main_IMV_appLogo);
    }
}
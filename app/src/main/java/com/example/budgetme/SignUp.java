package com.example.budgetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.budgetme.Activites.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    private EditText signUp_LBL_email, signUp_LBL_password, signUp_LBL_name;
    private MaterialTextView signUp_LBL_loginQ;
    private MaterialButton signUp_BTN_signUp, signUp_BTN_Login;
    private ImageView signUp_IMV_appLogo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        findViews();
        clickListeners();
    }

    private void clickListeners() {
        //when button pressed move to Login activity
        signUp_BTN_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        signUp_BTN_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String userEmail = signUp_LBL_email.getText().toString().trim();
        String password = signUp_LBL_password.getText().toString().trim();
        String userName = signUp_LBL_name.getText().toString().trim();

        if (userName.isEmpty()) {
            signUp_LBL_name.setError("Full name is required");
            Log.d("hello", "error");
            signUp_LBL_name.requestFocus();
            return;
        }
        if (userEmail.isEmpty()) {
            signUp_LBL_email.setError("email is required");
            signUp_LBL_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            signUp_LBL_password.setError("password is required");
            signUp_LBL_password.requestFocus();
            return;
        }
        if (password.length() < 6) {
            signUp_LBL_password.setError("Min password length should be 6 chars");
            signUp_LBL_password.requestFocus();
            return;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())) {
            signUp_LBL_email.setError("Please provide valid email");
            signUp_LBL_email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("tag", "Line 95 with user name "+ userName + " email " +userEmail);
                    User user = new User(userName, userEmail);
                    FirebaseDatabase.getInstance().getReference("Users").child(task.getResult().getUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "User has been Signed-Up successfully", Toast.LENGTH_LONG).show();

                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(SignUp.this, "Failed to Sign-Up user", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignUp.this, "Failed to Sign-Up user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void findViews() {
        signUp_LBL_email = findViewById(R.id.signUp_LBL_email);
        signUp_LBL_password = findViewById(R.id.signUp_LBL_password);
        signUp_BTN_signUp = findViewById(R.id.signUp_BTN_signUp);
        signUp_LBL_loginQ = findViewById(R.id.signUp_LBL_loginQ);
        signUp_BTN_Login = findViewById(R.id.signUp_BTN_Login);
        signUp_LBL_name = findViewById(R.id.signUp_LBL_name);
        signUp_IMV_appLogo = findViewById(R.id.signUp_IMV_appLogo);
    }
}
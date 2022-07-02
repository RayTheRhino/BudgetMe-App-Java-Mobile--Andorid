package com.example.budgetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetme.Activites.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText forgotpass_LBL_email;
    private MaterialButton forgotpass_BTN_Back, forgotpass_BTN_Reset;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mAuth = FirebaseAuth.getInstance();

        findViews();
        clickListners();
    }

    private void clickListners() {
        forgotpass_BTN_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(intent);
            }
        });
        forgotpass_BTN_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = forgotpass_LBL_email.getText().toString().trim();
        if(email.isEmpty()){
            forgotpass_LBL_email.setError("Email is required!");
            forgotpass_LBL_email.requestFocus();
            return;
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            forgotpass_LBL_email.setError("Please provide a valid Email");
            forgotpass_LBL_email.requestFocus();
            return;
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Check email to reset password",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ForgotPassword.this,"Tey again something wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void findViews() {
        forgotpass_LBL_email = findViewById(R.id. forgotpass_LBL_email);
        forgotpass_BTN_Back = findViewById(R.id.forgotpass_BTN_Back);
        forgotpass_BTN_Reset = findViewById(R.id.forgotpass_BTN_Reset);
    }
}
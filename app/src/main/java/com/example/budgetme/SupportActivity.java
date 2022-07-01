package com.example.budgetme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SupportActivity extends AppCompatActivity {

    private LinearLayout support_LAY_web, support_LAY_call, support_LAY_email;
    private TextView info_LBL;
    private EditText name_EDT, email_EDT;
    private MaterialButton send_BTN;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        clickListner();
    }

    private void clickListner() {
        send_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDeats();
            }
        });
    }

    private void sendDeats() {
        String email = name_EDT.getText().toString().trim();
        String name = email_EDT.getText().toString().trim();
        if (name.isEmpty()) {
            name_EDT.setError("Full name is required");
            Log.d("hello", "error");
            name_EDT.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            email_EDT.setError("email is required");
            email_EDT.requestFocus();
            return;
        }
        if ((Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            email_EDT.setError("Please provide valid email");
            email_EDT.requestFocus();
            return;
        }
        Toast.makeText(this, "Details sent successfully", Toast.LENGTH_SHORT).show();

    }

    private void findViews() {
        support_LAY_web = findViewById(R.id.support_LAY_web);
        support_LAY_call = findViewById(R.id.support_LAY_call);
        support_LAY_email = findViewById(R.id.support_LAY_email);
        info_LBL = findViewById(R.id.info_LBL);
        name_EDT = findViewById(R.id.name_EDT);
        email_EDT = findViewById(R.id.email_EDT);
        send_BTN = findViewById(R.id.send_BTN);
    }
}
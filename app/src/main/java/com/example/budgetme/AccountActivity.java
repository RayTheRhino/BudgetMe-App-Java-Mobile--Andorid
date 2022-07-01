package com.example.budgetme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    private TextView account_LBL_userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViews();
        account_LBL_userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    private void findViews() {
        account_LBL_userEmail = findViewById(R.id.account_LBL_userEmail);
    }
}
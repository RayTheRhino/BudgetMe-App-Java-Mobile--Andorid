package com.example.budgetme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

public class ChooseAnalyticActivity extends AppCompatActivity {
    private Toolbar main_ToolBar;
    private CardView analystic_CRD_today,analystic_CRD_week,analystic_CRD_month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytic);
        findViews();
        clickListners();
    }

    private void clickListners() {
        analystic_CRD_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseAnalyticActivity.this,DailyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
        analystic_CRD_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseAnalyticActivity.this,WeeklyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
        analystic_CRD_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseAnalyticActivity.this,MonthlyAnalyticsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        main_ToolBar = findViewById(R.id.main_ToolBar);
        analystic_CRD_today = findViewById(R.id.analystic_CRD_today);
        analystic_CRD_week = findViewById(R.id.analystic_CRD_week);
        analystic_CRD_month = findViewById(R.id.analystic_CRD_month);
    }
}